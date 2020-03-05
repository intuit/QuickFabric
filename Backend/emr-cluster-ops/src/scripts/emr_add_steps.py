# Lambda function to add steps to EMR cluster

import json

from src.util.log import setup_logging
from src.util.emrlib import get_emr_steps, emr_add_step, emr_validate_step

from src.util.exceptions import EMRClusterAddStepException, FileReadError
from src.util.commlib import check_file_exist
from src.util.commlib import construct_error_response
import os


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id', 'null')
    account = event.get('account')

    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name = event.get('cluster_name', None)
    cluster_id = event.get('cluster_id', None)
    step_name = event.get('step', None)
    cluster_type = event.get('cluster_type', 'unknown').lower()

    # Define error json response for APIs
    error_response = construct_error_response(context, api_request_id)

    # cluster name nonkerb-testing-prd1, role name would be testing
    role = cluster_name.split('-')[1]

    src_dir = os.path.dirname(os.path.dirname(__file__))
    metadata_file = f'{src_dir}/conf/{account}/emr-metadata.json'
    emr_config_file = f'{src_dir}/conf/{account}/emr-{cluster_type}-config.json'

    if not all([check_file_exist(metadata_file), check_file_exist(emr_config_file)]):
        error_response.update(Message='Metadata or config one of the file not found')
        print(FileReadError(path=metadata_file, message='Metadata or config one of the file not found'))
        print(FileReadError(path=emr_config_file, message='Metadata or config one of the file not found'))
        raise EMRClusterAddStepException(error_response)

    # Read emr config file
    with open(emr_config_file, 'r') as emr_conf_file:
        emr_config = json.load(emr_conf_file)['Cluster-Configurations']

    # Check if the passed role name exist the emr-config file
    if role in emr_config.keys():
        emr_role = role
    else:
        print(f"Role name '{role}' not defined in the EMR config...using default config")
        emr_role = 'default'

    role_config = emr_config[emr_role]

    # Read emr metadata file, which has account level settings like vpc, subnet
    with open(metadata_file) as account_metadata_file:
        metadata_config = json.load(account_metadata_file)

    emr_steps = []
    # Get the role specific bootstrap actions from role config
    emr_steps.extend(role_config.get('steps', []))
    # Get the cluster specific bootstrap actions from metadata config
    emr_steps.extend(metadata_config.get('steps', []))

    step_config = {}
    # Filter the step name
    for step in emr_steps:
        # Check if step with name exist in emr_steps list
        if step_name == step.get('Name'):
            step_config['steps'] = [step]
            break

    if len(step_config) < 1:
        logger.error(f"Unable to find step with name {step_name} in the metadata file or role config file")
        error_response.update(Message='No step found')
        raise EMRClusterAddStepException(error_response)
    metadata_config = {}
    metadata_config.update(steps=[])
    emr_step_config_list = get_emr_steps(role_config=step_config, metadata_config=metadata_config)

    try:
        step_id = emr_add_step(cluster_id, emr_step_config_list)
    except Exception as error:
        logger.error(f"Unable to add step with name {step_name} to emr cluster {cluster_id}")
        error_response.update(Message='EMR add step failed')
        raise EMRClusterAddStepException(error_response)

    return {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "cluster_type": cluster_type,
        "cluster_id": cluster_id,
        "step_id": step_id,
        "status": 'PENDING',
        "message": "Step has been submitted"
    }