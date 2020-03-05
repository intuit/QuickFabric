import pytest
import boto
from moto import mock_emr, mock_ec2, mock_ec2_deprecated
from aws_lambda_context import LambdaContext
from src.scripts.emr_create import lambda_handler as emr_create_cluster
import time
from moto.ec2.models import VPCBackend, VPC

ec2 = mock_ec2()
ec2.start()

emr = mock_emr()
emr.start()

cluster_create_request = {
        "api_request_id": "test_emr_create",
        "sub_type": "nonkerb",
        "role": "testing",
        "account": "example_account",
        "name": f"scheduled-testing-{int(time.time())}",
        "core_instance_count": "1",
        "task_instance_count": "3",
        "task_ebs_vol_size": "180",
        "custom_ami_id": "ami-075ac68c1cf8ba1c8",
        "bootstrap_actions": []
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
        response = emr_create_cluster(cluster_create_request, lambda_context)
        return response
