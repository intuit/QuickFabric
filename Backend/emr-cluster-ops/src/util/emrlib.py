"""
This module implements the common functions required for EMR cluster operations
"""

import botocore.exceptions
from boto3 import Session
from random import randint
import json
import os

from src.util import exceptions
from src.util.log import setup_logging
from src.util.exceptions import MissingEMRCluster, EMRClusterAddStepException, EMRClusterValidateStepException
from src.util import constants  # https://github.com/aws/aws-cli/blob/develop/awscli/customizations/emr/constants.py

logger = setup_logging('emrlib', '')

session = Session()
emr = session.client('emr')
ec2 = session.client('ec2')
ec2_resource = session.resource('ec2')
ce = session.client('ce')


def get_amazon_linux_ami():
    """
    Return the custom AMI based on an Amazon Linux AMI ID , HVM, EBS-Backed, 64-bit (the most used image),
    depending on region. This AMI will be used for creating EMR cluster.

    Check this page on how to find the Linux AMI:
    http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/finding-an-ami.html

    Here's how to filter images from the command line:
    aws --profile processing-prd ec2 --region us-west-2 describe-images --owner amazon \
    --query 'Images[?Name!=`null`]|[?starts_with(Name, `amzn-ami-hvm-2019`) == `true`].[CreationDate,ImageId,Name]' \
     --output text | sort -rk1

    :return: ami_id : string, Amazon Linux AMI ID
  """
    ami_id = ""

    # return a list of Image IDs for all HVM Amazon Linux AMIs with a 2018.03 version
    try:
        response = ec2.describe_images(
            Filters=[
                {
                    'Name': 'name',
                    'Values': [
                        'amzn-ami-hvm-2018.03' + '*' + '-ebs',
                    ]
                },
                {
                    'Name': 'architecture',
                    'Values': ['x86_64']
                }
            ],
            Owners=[
                'amazon'
            ]
        )

    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'describe_images')

    else:
        # Sort the response based on CreationDate to get the latest ami id based on creation date.
        ami_id = sorted(response['Images'], key=lambda image: image['CreationDate'])[-1]['ImageId']

    return ami_id


def get_cluster_id(cluster_name: str):
    """
    Return the EMR cluster ID for a given cluster name.
    :param cluster_name: string,  EMR cluster name which cluster_id to be returned
    :return: string, EMR cluster id
    """

    cluster_list = get_cluster_list()
    cluster_id = [cluster['cluster_id'] for cluster in cluster_list if cluster['cluster_name'] == cluster_name]

    # Convert cluster_id list to string
    cluster_id = ''.join(cluster_id)

    if not cluster_id.lower().startswith('j-'):
        raise MissingEMRCluster(cluster_name)

    return cluster_id


def get_cluster_name(cluster_id: str, terminated: bool = False):
    """
    Return the EMR cluster Name for a given cluster id.
    :param cluster_id: str, EMR cluster id which cluster_name to be returned
    :param terminated: bool, get terminated cluster names?
    :return: cluster_name: string,  EMR cluster name
    """

    cluster_list = get_cluster_list(terminated)
    cluster_name = [cluster['cluster_name'] for cluster in cluster_list if cluster['cluster_id'] == cluster_id]

    if len(cluster_name) > 0:
        # Convert cluster_name list to string
        cluster_name = ''.join(cluster_name)
    else:
        raise MissingEMRCluster(cluster_id)

    return cluster_name


def get_cluster_list(terminated: bool = False):
    """
    Returns a list of clusters that are in one of the state  ['STARTING','BOOTSTRAPPING','RUNNING','WAITING']

    :param terminated: bool, add terminated list of cluster to cluster list yes or no?
    :return: clusters: list, list of cluster IDs
    """
    clusters = []

    cluster_state = constants.LIST_CLUSTERS_STATES if terminated else constants.LIST_CLUSTERS_ACTIVE_STATES

    try:
        paginator = emr.get_paginator('list_clusters')

        for page in paginator.paginate(ClusterStates=cluster_state):
            for cluster in page['Clusters']:
                cluster = {
                    'cluster_id': cluster.get('Id'),
                    'cluster_name': cluster.get('Name'),
                }
                clusters.append(cluster)

    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'list_clusters')

    return clusters


def get_emr_cluster_status(cluster_id: str, detail: bool = False):
    """
    Provides cluster-level details including status, cluster_id, cluster_name and so on.
    Args:
        cluster_id: string, EMR cluster id
        detail: bool, provided additional detail about cluster like ec2 attributes

    Returns:
        cluster_status(): dict, cluster level details like id, name, state

    """
    cluster_status = {}

    try:
        response = emr.describe_cluster(ClusterId=cluster_id)

    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'describe_cluster')
    else:
        cluster_status = {
            "cluster_id": response.get('Cluster').get('Id'),
            "cluster_name": response.get('Cluster').get('Name'),
            "status": response.get('Cluster').get('Status').get('State'),
            "protection": response.get('Cluster').get('TerminationProtected'),
            "message": response.get('Cluster').get('Status').get('StateChangeReason').get('Message')
        }

    if detail:
        cluster_status['ec2_attributes'] = {
            'subnet_id': response.get('Cluster').get('Ec2InstanceAttributes').get('Ec2SubnetId'),
            'availability_zone': response.get('Cluster').get('Ec2InstanceAttributes').get('Ec2AvailabilityZone'),
            'master_sg': response.get('Cluster').get('Ec2InstanceAttributes').get('EmrManagedMasterSecurityGroup'),
            'service_sg': response.get('Cluster').get('Ec2InstanceAttributes').get('ServiceAccessSecurityGroup')
        }

    return cluster_status


def get_cluster_metadata(cluster_id: str):
    """
        Provides cluster-level metadata like master ip, rm url.
        Args:
            cluster_id (): string, EMR cluster id

        Returns:
            cluster_metadata(): dict, cluster metadata details like master ip, rm url

        """
    cluster_metadata = {}

    try:
        response = emr.list_instances(
            ClusterId=cluster_id,
            InstanceGroupTypes=['MASTER'])

    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'list_instances')
    else:
        if 'Instances' in response:
            cluster_metadata = {
                "rm_url": "http://{}:8088".format(
                    response['Instances'][len(response['Instances']) - 1]['PrivateDnsName']),
                "master_ip": response['Instances'][len(response['Instances']) - 1]['PrivateIpAddress']
            }
        else:
            cluster_metadata = {
                "rm_url": "None",
                "master_ip": "None"
            }

    return cluster_metadata


def create_emr_cluster(user_inputs: dict, config_file: str, metadata_file: str, role: str, cluster_type: str, cluster_name: str):
    """
    Creates and start running a new EMR cluster with provided settings.
    :param user_inputs: (dict), user entered inputs via Quickfabric UI.
    :param config_file: (str), emr config file path
    :param metadata_file: (str), account level metadata path
    :param role: (str), emr role name
    :param cluster_type: (str), type of the cluster to be created kerb/nonkerb
    :param cluster_name: (str), name of the emr cluster
    :return: None
    """

    try:
        cluster_config_params = get_emr_cluster_settings(user_inputs, config_file, metadata_file, role, cluster_name)
    except Exception as error:
        logger.error(error)
        raise

    logger.info("Starting EMR cluster with these settings", cluster_config_params)

    try:
        # FINALLY!! run the cluster and watch it burn
        cluster = emr.run_job_flow(**cluster_config_params)

    except botocore.exceptions.ClientError as error:
        print(error)
        raise

    if cluster.get('ResponseMetadata').get('HTTPStatusCode') == 200:
        logger.info("cluster creation is successful..")
        return cluster.get('JobFlowId', None)
    else:
        logger.error("cluster creation failed ..")
        return None


def get_emr_cluster_settings(user_inputs: dict, config_file: str, metadata_file: str, role: str, cluster_name: str):
    """
    Reads a json file passed in as a parameter, fills in defaults
    where appropriate, then returns a standard dictionary structure
    that can be used further to run various operations on EMR clusters,
    like launch the cluster, add steps to it, etc.

    Args:
        user_inputs (dict): user entered inputs via Quickfabric UI.
        config_file (str): full path to the file containing cluster settings
        metadata_file (str): full path to the cluster metadata setting
        role (str): emr role name
        cluster_name (str): emr cluster name
    Returns:
        a dictionary containing cluster parameters. A value should be provided
        for each setting, whether it's a default value or empty, unless it is a
        required setting, in which case an exception will be raised if it is
        missing from the file
    """

    # Read emr config file
    with open(config_file, 'r') as emr_conf_file:
        emr_config = json.load(emr_conf_file)['Cluster-Configurations']

    # Check if the passed role name exist the emr-config file
    if role in emr_config.keys():
        emr_role = role
    else:
        logger.info(f"Role name '{role}' not defined in the EMR config...using default config")
        emr_role = 'default'

    role_config = emr_config[emr_role]
    default_config = emr_config['InstanceGroup-Configurations']

    # Read emr metadata file, which has account level settings like vpc, subnet
    with open(metadata_file) as account_metadata_file:
        metadata_config = json.load(account_metadata_file)

    required_keys = ['vpc_id', "private_subnet_ids", 'emr_version', 'keypair']

    missing_keys = []
    for key in required_keys:
        if not key in metadata_config:
            missing_keys.append(key)
    if len(missing_keys) != 0:
        raise KeyError('Required settings not found in file: ', metadata_file, missing_keys)

    # BOOTSTRAP ACTIONS are going to be a list, similar to steps.
    # required settings/configurations should be inserted at the start of the
    src_dir = os.path.dirname(os.path.dirname(__file__))
    emr_sw_config_file = f'{src_dir}/conf/common/configurations.json'
    with open(emr_sw_config_file) as config_json:
        configuration = json.load(config_json)

    try:
        # EMR construct config parameters
        parameters = {
            "Name": cluster_name,
            "LogUri": role_config.get('EMRS3LogPath') or metadata_config.get('emr_s3_log_path'),
            "Configurations": configuration,
            "ReleaseLabel": role_config.get('emr_version') or metadata_config.get('emr_version'),
            "Instances": get_instance_configuration(user_inputs, role_config, default_config, metadata_config,
                                                    cluster_name),
            "Applications": get_app_list(role_config.get('app_list')),
            "JobFlowRole": role_config.get('EC2Role') or constants.EC2_ROLE_NAME,
            "ServiceRole": role_config.get('EMRRole') or constants.EMR_ROLE_NAME,
            "AutoScalingRole": role_config.get('EC2Role') or constants.EMR_AUTOSCALING_ROLE_NAME,
            "BootstrapActions": get_bootstrap_actions(user_inputs, role_config, metadata_config),
            "Steps": get_emr_steps(role_config, metadata_config),
            "Tags": tag_list(role_config, metadata_config, cluster_name),
            "VisibleToAllUsers": True,
            "CustomAmiId": user_inputs.get('custom_ami_id') or metadata_config.get(
                'custom_ami') or get_amazon_linux_ami()
        }
    except Exception as exception:
        logger.error("Error building EMR cluster creation parameters", exception)
        raise exceptions.MissingParametersError(f"Error building EMR cluster creation parameters {str(exception)}")

    return parameters


def get_instance_configuration(user_inputs: dict, role_config: dict, default_config: dict, metadata_config: dict,  cluster_name: str):
    """
    EMR instance groups to be used for instance group and autoscaling

    :param user_inputs: (dict), user provided inputs through UI
    :param role_config: (dict), role specific configured defined in emr-<clusterType>-config.json
    :param default_config: (dict), default configs from emr configs
    :param metadata_config: (dict), account level metadata configs gtom emr-metadata.json
    :param cluster_name: (str), emr cluster name
    """

    emr_master_slave_sg_name = f'emr-master-slave-security-group-{cluster_name}'
    emr_master_slave_sg_description = f'EMR managed Security Group for {cluster_name}'

    emr_service_access_sg_name = f'emr-service-access-security-group-{cluster_name}'
    emr_service_access_sg_description = f'EMR managed Service Access Security Group for {cluster_name}'

    # Verify master and service security group already exist for the cluster name, if yes, use it
    emr_master_slave_sg_id = describe_security_group(emr_master_slave_sg_name, metadata_config['vpc_id'])
    emr_service_access_sg_id = describe_security_group(emr_service_access_sg_name, metadata_config['vpc_id'])

    if not emr_master_slave_sg_id:
        logger.info('Running step: create the emr_master_slave_sg')
        response = create_security_group(emr_master_slave_sg_name, emr_master_slave_sg_description,
                                         metadata_config['vpc_id'])
        emr_master_slave_sg_id = response.get('GroupId')

    if not emr_service_access_sg_id:
        logger.info('Running step: create the emr_service_access_sg')
        response = create_security_group(emr_service_access_sg_name, emr_service_access_sg_description,
                                         metadata_config['vpc_id'])
        emr_service_access_sg_id = response.get('GroupId')

    # Get vpc cidr range
    vpc_cidr = get_vpc_cidr(metadata_config['vpc_id'])
    # Add ingress rule on master sg so that RM proxy lambda can access RM manager and Dr Elephant api via port 8088 & 8087
    if vpc_cidr:
        # Resource manager port
        add_ingress_rule(emr_master_slave_sg_id, vpc_cidr, 8088)
        # Dr elephant port
        add_ingress_rule(emr_master_slave_sg_id, vpc_cidr, 8087)

    instances = {
        "Ec2KeyName": role_config.get('keypair') or metadata_config.get('keypair') or '',
        'KeepJobFlowAliveWhenNoSteps': role_config.get('KeepClusterRunning') or True,
        'TerminationProtected': False,
        # Randomly pick a subnet id from list of subnet
        'Ec2SubnetId': metadata_config.get('private_subnet_ids')[randint(0, len(metadata_config.get('private_subnet_ids')) - 1)],
        'EmrManagedMasterSecurityGroup': emr_master_slave_sg_id,
        'EmrManagedSlaveSecurityGroup': emr_master_slave_sg_id,
        'AdditionalMasterSecurityGroups': role_config.get('MasterSecurityGroups', []),
        'ServiceAccessSecurityGroup': emr_service_access_sg_id,
        'AdditionalSlaveSecurityGroups': role_config.get('SlaveSecurityGroups', []),
    }

    master_nodes_config = {
        "Name": "Master nodes",
        "Market": "ON_DEMAND",
        "InstanceRole": "MASTER",
        "InstanceType": user_inputs.get('master_instance_type') or role_config.get(
            'MasterInstanceType') or default_config.get('default_master_group').get('InstanceType'),
        "InstanceCount": int(
            role_config.get('MasterInstanceCount') or default_config.get('default_master_group').get('InstanceCount')),
        "EbsConfiguration": {
            "EbsBlockDeviceConfigs": [
                {
                    "VolumeSpecification": {
                        "VolumeType": "gp2",
                        "SizeInGB": int(
                            user_inputs.get('master_ebs_vol_size') or role_config.get('MasterEbsVolumeSizeGB', 100))
                    }
                }
            ],
            "EbsOptimized": True
        }
    }
    instance_group_list = [master_nodes_config]

    # Slave nodes (dictionary)
    # Set default EBS volumes for Core Nodes
    if user_inputs.get('core_instance_count') == "0":
        logger.info("Core node will not be attached to cluster..")
    else:
        core_instance_type = user_inputs.get('core_instance_type') or role_config.get('CoreInstanceType') or default_config.get('default_core_group').get('InstanceType')
        core_instance_count = int(user_inputs.get('core_instance_count') or role_config.get('CoreInstanceCount') or default_config.get('default_core_group').get('InstanceCount'))
        core_nodes_config = {
            "Name": "Core Nodes",
            "Market": "ON_DEMAND",
            "InstanceRole": "CORE",
            "InstanceType": core_instance_type,
            "InstanceCount": core_instance_count,
            "EbsConfiguration": {
                "EbsBlockDeviceConfigs": [
                    {
                        'VolumeSpecification': {
                            'VolumeType': 'gp2',
                            'SizeInGB': int(
                                user_inputs.get('core_ebs_vol_size') or role_config.get('CoreEbsVolumeSizeGB') or 50)

                        },
                        "VolumesPerInstance": 1
                    }
                ],
                "EbsOptimized": True
            }
        }
        instance_group_list.append(core_nodes_config)

    # TASK nodes (dictionary)
    # Set default EBS volumes for Task Nodes
    if user_inputs.get('task_instance_count') == "0":
        logger.info("Task node will not be attached to cluster..")
    else:
        task_instance_type = user_inputs.get('task_instance_type') or role_config.get(
                        'TaskInstanceType') or default_config.get('default_task_group').get('InstanceType')
        task_instance_count = int(
                        user_inputs.get('task_instance_count') or role_config.get('TaskInstanceCount') or default_config.get(
                            'default_task_group').get('InstanceCount'))
        task_nodes_config = {
            "Name": "Task nodes",
            "Market": "ON_DEMAND",
            "InstanceRole": "TASK",
            "InstanceType": task_instance_type,
            "InstanceCount": task_instance_count,
            "EbsConfiguration": {
                "EbsBlockDeviceConfigs": [
                    {
                        'VolumeSpecification': {
                            'VolumeType': 'gp2',
                            'SizeInGB': int(
                                user_inputs.get('task_ebs_vol_size') or role_config.get('TaskEbsVolumeSizeGB') or 50)
                        },
                        "VolumesPerInstance": 1
                    }
                ],
                "EbsOptimized": True
            }
        }
        instance_group_list.append(task_nodes_config)

    # make list of all instance groups

    instances['InstanceGroups'] = instance_group_list

    return instances


def get_app_list(apps: list):
    """
    Convert list of apps to boto3 compatible list of dicts
    :param apps: (list): list of apps to be installed in EMR
    :return: applist: (dict): list of app in dict format
    """
    # Default apps will be installed
    applist = list([{"Name": "Hadoop"}, {"Name": "Spark"}, {"Name": "Hive"}, {"Name": "Pig"}, {"Name": "Ganglia"}])
    for app in apps:
        if len(app.strip()) > 2:
            applist.append({"Name": app.strip()})
    return applist


def create_security_group(group_name: str, description: str, vpc_id: str):
    """
    Creates a security group under a given VPC ID.

    Args:
        group_name (str): The name of the security group.
        description (str): A description for the security group.
        vpc_id (str): The ID of the VPC

    Returns:
        response (dict)
        create the security group under the the VPC, and return id of the security group.
    """

    try:
        response = ec2.create_security_group(GroupName=group_name,
                                         Description=description,
                                         VpcId=vpc_id)
    except Exception as error:
        logger.error("Unable to create security group.")
        logger.error(error)
    else:
        return response


def empty_sg_rules(group_id: str):
    """
    Remove all ingress and egress rules from a security group.
    Args:
        group_id (str): The ID of the security group.

    Returns:
        None
    """
    sg_resource = ec2_resource.SecurityGroup(group_id)
    # Remove all ingress and egress rules before deleting the security group
    try:
        if sg_resource.ip_permissions:
            sg_resource.revoke_ingress(IpPermissions=sg_resource.ip_permissions)
        if sg_resource.ip_permissions_egress:
            sg_resource.revoke_egress(IpPermissions=sg_resource.ip_permissions_egress)
    except Exception as error:
        logger.error("Unable to remove security group rules.", error)
        raise botocore.exceptions.ClientError(error, 'revoke_ingress')


def delete_security_group(group_id: str):
    """
    Delete a security group
    Args:
        group_id (str): The ID of the security group.

    Returns: None

    """
    logger.info(f"Trying to delete security group {group_id}")
    try:
        response = ec2.delete_security_group(
            GroupId=group_id)
    except botocore.exceptions.ClientError as error:
        logger.error(error)
    else:
        logger.info(f"Security group {group_id} deleted successfully..")


def tag_list(role_config: dict, metadata_config: dict, cluster_name: str):
    """
    Create list of ec2 tags from role config and metadata condif
    Args:
        role_config (dict): EMR role config
        metadata_config (dict): Account level metadata config
        cluster_name (str): EMR cluster name

    Returns:
        Returns list of dict, each dict is an ec2 tag object.
    """
    role_tags = []

    for tag in role_config.get('tags', []):
        role_tags.append({
            "Key": tag,
            "Value": role_config.get('tags').get(tag)
        })

    account_level_tags = []
    for tag in metadata_config.get('tags', []):
        account_level_tags.append({
            "Key": tag,
            "Value": metadata_config.get('tags').get(tag)
        })

    # Default tags added to ec2 instance and emr
    tags = [
        {
            "Key": "Name",
            "Value": cluster_name
        },
        {
            "Key": "Tool",
            "Value": 'Quickfabric'
        },
        {
            "Key": "emr:cluster:type",
            "Value": cluster_name.split('-')[0]
        }
    ]
    tags.extend(role_tags)
    tags.extend(account_level_tags)
    return tags


def terminate_emr_cluster(cluster_name: str, cluster_id: str = None, force: bool =False):
    """
    Terminate and EMR cluster if cluster_id parameter is same as cluster_id fetch programmatically

    Args:
        cluster_name (str): Name of the EMR cluster
        cluster_id (str): The ID of the EMR cluster
        force   (bool): Forcefully delete a cluster if termination protection is enabled
    """
    cluster_id_to_terminate = ''

    try:
        cluster_id_to_terminate = get_cluster_id(cluster_name)
    except Exception as error:
        logger.error("An error occurred while fetching the cluster id", error)
    else:
        logger.info(f"Cluster ID = {cluster_id_to_terminate}")

    if cluster_id.lower().startswith('j-') and cluster_id.lower() == cluster_id_to_terminate.lower():
        logger.info(
            f"Cluster id passed {cluster_id} is same as derived cluster id {cluster_id_to_terminate}, trying to terminate the cluster.")
    else:
        logger.info(f"Cluster id passed {cluster_id} does not match with derived cluster id {cluster_id_to_terminate}.")
        raise Exception(
            f"Cluster id passed {cluster_id} does not match with derived cluster id {cluster_id_to_terminate}.")

    if force:
        emr.set_termination_protection(JobFlowIds=[cluster_id],
                                       TerminationProtected=False)

    try:
        emr.terminate_job_flows(JobFlowIds=[cluster_id])
    except Exception as error:
        logger.error("Unable to terminate cluster", error)
        raise botocore.exceptions.ClientError("Cluster termination failed", 'emr:terminate_job_flows')


def get_bootstrap_actions(user_inputs: dict, role_config: dict, metadata_config: dict):
    """Converts step to boto3 structure.

    Converts an EMR 'bootstrap actions' from the simplified dict format to the syntax
    needed to pass a step to boto3 EMR client methods like run_job_flow()

    Args:
        user_inputs (dict): User entered bootstrap actions from quickfabric UI.
        role_config (dict): Bootstrap actions defined in role definition in emr-config file.
        metadata_config (dict): bootstrap actions defined in emr-metadata file.

    Returns:
        A list element that can be added as bootstrap action to an EMR cluster.
    """

    # user_inputs passed directly from quickfabric UI
    if not isinstance(user_inputs, dict):
        raise TypeError('Parameter should be a dict, but we received ', type(user_inputs))

    # Role config and bootstrap configs are always dict, check that we received a dictionary, otherwise raise exception
    if not isinstance(role_config, dict):
        raise TypeError('Parameter should be a dict, but we received ', type(role_config))

    if not isinstance(metadata_config, dict):
        raise TypeError('Parameter should be a dict, but we received ', type(metadata_config))

    emr_bas = []
    # Get the bootstrap actions from user inputs
    bootstrap_user_input = user_inputs.get('bootstrap_actions')
    for elem in bootstrap_user_input:
        emr_bas.extend(elem)

    # Get the role specific bootstrap actions from role config
    emr_bas.extend(role_config.get('bootstrap_actions', []))
    # Get the cluster specific bootstrap actions from metadata config
    emr_bas.extend(metadata_config.get('bootstrap_actions', []))

    # check for keys that we require, raise exception if not provided
    required_keys = ['bootstrapName', 'bootstrapScript']

    for ba in emr_bas:
        if set(ba.keys()) & set(required_keys) != set(required_keys):
            missing_keys = list(set(required_keys) - set(ba.keys()))
            raise KeyError('Required keys are missing this step: ', missing_keys, ba)

    # Convert steps to boto3 compatible
    ba_config_list = []

    # Example bootstrap actions
    """
        "bootstrapActions": [
                {
                "bootstrapName": "boot1",
                "bootstrapScript": "s3://my_dir/run_my_script.sh param1"
                }
            ]
    """
    for ba in emr_bas:
        ba_config = {}
        ba_config = build_bootstrap(ba, metadata_config)
        ba_config_list.append(ba_config)

    return ba_config_list


def get_emr_steps(role_config: dict = None, metadata_config: dict = None, onboot: bool = False):
    """Converts step to boto3 structure.
    onverts an EMR 'step' from the simplified dict format to the syntax needed to pass a step to boto3 EMR
    client for add_job_flow_steps().

    Args:
        role_config (dict): Steps defined the role file
        metadata_config (dict): Steps defined the account level metadata config file
        onboot (bool): Add onboot steps or not

    Returns:
        a list element that can be added to the list of steps the cluster should
        execute
    """

    # Role config and bootstrap configs are always dict, check that we received a dictionary, otherwise raise exception
    if metadata_config is None:
        metadata_config = {}

    if role_config is None:
        role_config = {}

    if not isinstance(role_config, dict):
        raise TypeError('Parameter should be a dict, but we received ', type(role_config))

    if not isinstance(metadata_config, dict):
        raise TypeError('Parameter should be a dict, but we received ', type(metadata_config))

    emr_steps = []
    # Get the role specific bootstrap actions from role config
    emr_steps.extend(role_config.get('steps', []))
    # Get the cluster specific bootstrap actions from metadata config
    emr_steps.extend(metadata_config.get('steps', []))

    # check for keys that we require, raise exception if not provided
    required_keys = ['Name', 'Type']

    for step in emr_steps:
        if set(step.keys()) & set(required_keys) != set(required_keys):
            missing_keys = list(set(required_keys) - set(step.keys()))
            raise KeyError('Required keys are missing this step: ', missing_keys, step)

    # Convert steps to boto3 compatible
    step_config_list = []

    for step in emr_steps:
        step_type = step.get('Type')
        if step_type is None:
            step_type = constants.CUSTOM_JAR

        step_type = step_type.lower()
        step_config = {}

        if step_type == constants.CUSTOM_JAR:
            step_config = build_custom_jar_step(parsed_step=step)
        elif step_type in constants.STEP_TYPE_SHELL:
            step_config = build_script_step(parsed_step=step, metadata=metadata_config)
        elif step_type == constants.HIVE:
            pass
        elif step_type == constants.PIG:
            pass
        elif step_type == constants.IMPALA:
            pass
        elif step_type == constants.SPARK:
            pass
        else:
            raise exceptions.UnknownStepTypeError(step_type=step_type)

        step_config_list.append(step_config)

    return step_config_list


def build_custom_jar_step(parsed_step: dict):
    name = _apply_default_value(
        arg=parsed_step.get('Name'),
        default=constants.DEFAULT_CUSTOM_JAR_STEP_NAME)

    action_on_failure = _apply_default_value(
        arg=parsed_step.get('ActionOnFailure'),
        default=constants.DEFAULT_FAILURE_ACTION)

    check_required_field(
        structure=constants.CUSTOM_JAR_STEP_CONFIG,
        name='Jar', value=parsed_step.get('Jar'))

    return build_step(
        jar=parsed_step.get('Jar'),
        args=parsed_step.get('Args'),
        name=name,
        action_on_failure=action_on_failure)


def build_script_step(parsed_step, metadata):
    name = _apply_default_value(
        arg=parsed_step.get('Name'),
        default=constants.DEFAULT_SCRIPT_STEP_NAME)

    action_on_failure = _apply_default_value(
        arg=parsed_step.get('ActionOnFailure'),
        default=constants.DEFAULT_FAILURE_ACTION)

    script_jar = _apply_default_value(arg=parsed_step.get('Jar'), default=constants.SCRIPT_RUNNER_JAR)

    # If complete s3 path is not provided for the script, pull the script from artifacts_bucket
    if not 's3://' in parsed_step.get('Script'):
        script_path = '{}/{}'.format(metadata.get('artifacts_bucket'), parsed_step.get('Script'))
    else:
        script_path = parsed_step.get('Script')

    script_args = [f"{script_path} {' '.join(parsed_step.get('Args'))}".strip()]

    return build_step(
        jar=script_jar,
        args=script_args,
        name=name,
        action_on_failure=action_on_failure)


def _apply_default_value(arg, default):
    return arg or default


def build_step(jar, name='Step', action_on_failure=constants.DEFAULT_FAILURE_ACTION, args=None, main_class=None,
               properties=None):
    check_required_field(structure='HadoopJarStep', name='Jar', value=jar)
    step = {}
    apply_dict(step, 'Name', name)
    apply_dict(step, 'ActionOnFailure', action_on_failure)
    jar_config = {}
    jar_config['Jar'] = jar
    apply_dict(jar_config, 'Args', args)
    apply_dict(jar_config, 'MainClass', main_class)
    apply_dict(jar_config, 'Properties', properties)
    step['HadoopJarStep'] = jar_config

    return step


def build_bootstrap(ba, metadata):
    """ Helper function to construct BA action """
    ba_name = ba.get('bootstrapName')
    ba_path_with_args = ba.get('bootstrapScript').split()
    ba_path = ba_path_with_args[0]
    ba_args = ba_path_with_args[1:]

    if not 's3://' in ba_path:
        ba_path = '{}/{}'.format(metadata.get('artifacts_bucket'), ba_path)

    if ba_path != '' and ba_args != '':
        ba = {"Name": ba_name,
              "ScriptBootstrapAction": {
                  "Path": ba_path,
                  "Args": ba_args
              }
              }
    elif ba_path != '':
        ba = {"Name": ba_name,
              "ScriptBootstrapAction": {
                  "Path": ba_path
              }
              }
    else:
        ba = {}
    return ba


def apply_dict(params, key, value):
    if value:
        params[key] = value

    return params


def check_required_field(structure, name, value):
    if not value:
        raise exceptions.MissingParametersError(
            object_name=structure, missing=name)


def convert_to_lower(list_keys):
    lowered_list_keys = [key.lower() for key in list_keys]
    return lowered_list_keys


def emr_add_step(cluster_id: str, step_config: list):
    """
        Adds new steps to a running EMR cluster
    Args:
        cluster_id (str): A string that uniquely identifies the EMR cluster
        step_config (list): List contains emr step config

    Returns:
        Return the step ids once EMR step submission is successful.

    """

    try:
        response = emr.add_job_flow_steps(JobFlowId=cluster_id, Steps=step_config)
    except botocore.exceptions.ClientError as error:
        logger.error("Unable to add step", error)
        raise EMRClusterAddStepException(error)
    else:
        step_id = "".join(response.get('StepIds'))

    return step_id


def emr_validate_step(cluster_id: str, step_id: str):
    """
    Get more detailed EMR step
    Args:
        cluster_id (str): A string that uniquely identifies the EMR cluster
        step_id (str): EMR step id

    Returns:
        Returns a dict object with step status
    """
    step_status = {}
    try:
        response = emr.describe_step(ClusterId=cluster_id, StepId=step_id)
    except botocore.exceptions.ClientError as error:
        raise EMRClusterValidateStepException(error)
    else:
        step_status = {
            'step_id': response.get('Step').get('Id'),
            'step_name': response.get('Step').get('Name'),
            'status': response.get('Step').get('Status').get('State'),
            'message': response.get('Step').get('Status', {}).get('FailureDetails', {}).get('Message')
        }
    return step_status


def emr_cost_usage(start_date: str, end_date: str, cluster_name: str):
    """
        Get the EMR cost usage mont to date.
    Args:
        start_date (str):  The beginning of the time period that you want the usage and costs for. The start date is inclusive.
        end_date (str): The end of the time period that you want the usage and costs for. The end date is exclusive.
        cluster_name (str):  EMR cluster name

    Returns:
         cost_usage, dict object contain total emr cost usage month to date.
    """
    cost_usage = {}
    try:
        response = ce.get_cost_and_usage(
            TimePeriod={
                'Start': start_date,
                'End': end_date

            },
            Granularity='MONTHLY',
            Filter={
                "And":
                    [{"Dimensions": {"Key": "SERVICE",
                                     "Values": ["Amazon Elastic Compute Cloud - Compute", "Amazon Elastic MapReduce"]}},
                     {"Tags": {"Key": "Name", "Values": [cluster_name]}}]},
            Metrics=['UnblendedCost']
        )
    except Exception as error:
        raise botocore.exceptions.ClientError(error, 'get_cost_and_usage')
    else:
        cost_usage = {
            'Start_date': response.get('ResultsByTime')[0].get('TimePeriod', {}).get('Start'),
            'End_date': response.get('ResultsByTime')[0].get('TimePeriod', {}).get('End'),
            'Total_cost': '%.3f' % float(
                response.get('ResultsByTime')[0].get('Total', {}).get('UnblendedCost').get('Amount')),
        }

    return cost_usage


def set_emr_termination_protection(cluster_id: str, set_protection: bool):
    """
    Set or unset Termination Protection lock of a cluster.
    Args:
        cluster_id (str): A string that uniquely identifies the EMR cluster
        set_protection (bool): enable or disable the Termination Protection

    Returns:
        Returns True if protection is successful or else return false.
    """
    try:
        emr.set_termination_protection(
            JobFlowIds=[
                cluster_id
            ],
            TerminationProtected=set_protection
        )
    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'emr_set_termination_protection')

    return True


def describe_security_group(group_name: str, vpc_id: str):
    """
    Get the security ID of the specified security group

    :param group_name: str, EC2 security group name
    :param vpc_id: str, VPC ID where security group resides

    :return: str, security group id of provided security group name
    """

    response = {}

    try:
        response = ec2.describe_security_groups(Filters=[{
                'Name': 'vpc-id',
                'Values': [vpc_id]
            },
            {
                'Name': 'group-name',
                'Values': [group_name]
            },
        ])
    except Exception as error:
        logger.error(f"Unable to describe the group name {group_name}", error)
        raise botocore.exceptions.ClientError(str(error), 'describe_security_groups')

    if len(response.get('SecurityGroups')):
        sg_id = response.get('SecurityGroups')[0]['GroupId']
    else:
        sg_id = None

    return sg_id


def get_vpc_cidr(vpc_id: str):
    """
    Get VPC IP CIDR range of specified VPC
    :param vpc_id: str,  The Vpc's id identifier
    :return: str, VPC CIDR IP range
    """
    try:
        vpc = ec2_resource.Vpc(vpc_id)
        cidr = vpc.cidr_block
    except Exception as error:
        logger.error('Error occured while describing the VPC')
    else:
        return cidr
    return None


def add_ingress_rule(group_id: str, cidr_range: str, port: int):
    """
    Adds the specified ingress rules to a security group.
    :param group_id: str, ec2 security group ID
    :param cidr_range: str, The IPv4 CIDR range. You can either specify a CIDR range or a source security group, not both.
    :param port: int, TCP port number to be added to ingress rule.
    :return: None
    """
    sg = ec2_resource.SecurityGroup(group_id)
    # Add an ingress rule to a security group
    try:
        response = sg.authorize_ingress(
            CidrIp=cidr_range,
            FromPort=port,
            IpProtocol='TCP',
            ToPort=port,
        )
    except Exception as error:
        logger.error("Unable to add security group rules.", error)
    else:
        logger.info(f'Ingress rule for ip range {cidr_range} on port {port} added group id {group_id}')


def get_network_interface_association(sg_group_id: str):
    """
    Get the list of network interface attached to an EC2 Security Group.
    Args:
        sg_group_id (str): EC2 security group id

    Returns:
        Return True if security ID is attached to a interface else return False
    """
    response = {}

    try:
        response = ec2.describe_network_interfaces(
            Filters=[
                {
                    'Name': 'group-id',
                    'Values': [sg_group_id]
                },
            ]
        )
    except Exception as error:
        logger.error(str(error))

    if len(response.get('NetworkInterfaces', [])) > 0:
        return True
    else:
        return False

