import pytest
import boto3
from boto.emr.instance_group import InstanceGroup
from moto import mock_emr, mock_ec2
from aws_lambda_context import LambdaContext
import time

ec2 = mock_ec2()
ec2.start()

emr = mock_emr()
emr.start()

args = dict(
    Instances={
        "InstanceCount": 3,
        "KeepJobFlowAliveWhenNoSteps": True,
        "MasterInstanceType": "c3.medium",
        "Placement": {"AvailabilityZone": "us-west-2a"},
        "SlaveInstanceType": "c3.xlarge",
    },
    JobFlowRole="EMR_EC2_DefaultRole",
    LogUri="s3://mybucket/log",
    ServiceRole="EMR_DefaultRole",
    VisibleToAllUsers=True,
)

bootstrap_actions = [
        {
            "Name": "bs1",
            "ScriptBootstrapAction": {
                "Args": ["arg1", "arg2"],
                "Path": "s3://path/to/script",
            },
        },
        {
            "Name": "bs2",
            "ScriptBootstrapAction": {"Args": [], "Path": "s3://path/to/anotherscript"},
        },
    ]

input_steps = [
        {
            "HadoopJarStep": {
                "Args": [
                    "hadoop-streaming",
                    "-files",
                    "s3://elasticmapreduce/samples/wordcount/wordSplitter.py#wordSplitter.py",
                    "-mapper",
                    "python wordSplitter.py",
                    "-input",
                    "s3://elasticmapreduce/samples/wordcount/input",
                    "-output",
                    "s3://output_bucket/output/wordcount_output",
                    "-reducer",
                    "aggregate",
                ],
                "Jar": "command-runner.jar",
            },
            "Name": "My wordcount example",
        },
        {
            "HadoopJarStep": {
                "Args": [
                    "hadoop-streaming",
                    "-files",
                    "s3://elasticmapreduce/samples/wordcount/wordSplitter2.py#wordSplitter2.py",
                    "-mapper",
                    "python wordSplitter2.py",
                    "-input",
                    "s3://elasticmapreduce/samples/wordcount/input2",
                    "-output",
                    "s3://output_bucket/output/wordcount_output2",
                    "-reducer",
                    "aggregate",
                ],
                "Jar": "command-runner.jar",
            },
            "Name": "My wordcount example2",
        },
    ]

input_instance_groups = [
    InstanceGroup(1, "MASTER", "c1.medium", "ON_DEMAND", "master"),
    InstanceGroup(3, "CORE", "c1.medium", "ON_DEMAND", "core"),
    InstanceGroup(6, "TASK", "c1.large", "ON_DEMAND", "task")
]


cluster_create_request = {
        "api_request_id": "test_emr_create",
        "sub_type": "nonkerb",
        "role": "testing",
        "account": "example_account",
        "name": f"scheduled-testing-{int(time.time())}",
        "core_instance_count": "1",
        "task_instance_count": "3",
        "task_ebs_vol_size": "180",
        "custom_ami_id": "ami-075ac68c1cf8ba1c8"
}

@pytest.fixture
def lambda_context():
        context = LambdaContext()
        context.aws_request_id = 'test_aws_request_id'
        context.function_name = 'emr_create_function_name'
        context.log_group_name = 'log_group_name'
        context.log_stream_name = 'log_stream_name'
        return context


@pytest.fixture
@mock_ec2
@mock_emr
def create_cluster(lambda_context):
    client = boto3.client("emr", region_name='us-west-2')
    args['Name'] = cluster_create_request.get('name')
    args["BootstrapActions"] = bootstrap_actions
    args["Steps"] = input_steps
    cluster_id = client.run_job_flow(**args)["JobFlowId"]
    resp = client.describe_job_flows(JobFlowIds=[cluster_id])["JobFlows"][0]
    return resp



