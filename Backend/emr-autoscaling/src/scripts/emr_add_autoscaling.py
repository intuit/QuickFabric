# Lambda function to add auto-scaling policies to EMR cluster

import json
import os
from src.util.log import setup_logging
from src.util import exceptions
from src.util.emrlib import get_instance_group_by_name, add_emr_auto_scaling


def lambda_handler(event, context):
    api_request_id = event.get('api_request_id', 'null')
    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_id = event.get('cluster_id')
    instance_group = event.get('instance_group') or "TASK"
    autoscaling_profile = event.get('autoscaling_profile') or 'Default'
    min_count = event.get('min') or 0
    max_count = event.get('max') or 0
    instance_count = event.get('instance_count') or 0

    # Define error json response for APIs
    error_response = {
        "statusCode": 500,
        "lambda_function_name": context.function_name,
        "log_group_name": context.log_group_name,
        "log_stream_name": context.log_stream_name,
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id
    }

    as_minsize = -1
    as_maxsize = -1
    src_dir = os.path.dirname(os.path.dirname(__file__))
    asg_config = f'{src_dir}/conf/autoscaling-config.json'

    # Parse autoscaling config json file
    try:
        with open(asg_config) as json_file:
            emr = json.load(json_file)
        emr_as_template = emr['Autoscaling_Policy_Template']
        emr_as_profiles = emr['Autoscaling_Profiles']
    except Exception as error:
        logger.error("Unable to parse emr config json.", error)
        logger.error(exceptions.FileReadError(path=asg_config, message='Metadata or config one of the file not found'))
        error_response.update(Message='Creation of Auto-Scaling policies failed')
        raise exceptions.EMRClusterAutoScalingException(error_response)

    if autoscaling_profile not in emr_as_profiles.keys():
        autoscaling_profile = 'Default'

    as_profile = emr_as_profiles.get(autoscaling_profile.capitalize())
    as_minsize = as_profile.get('min')
    as_maxsize = as_profile.get('max')

    # If no profile was specified, check if explicit min and max values were passed ...
    if (int(min_count) > 0 ) and (int(max_count) > 0):
        as_minsize = min_count
        as_maxsize = max_count

    # If no explicit values were passed, try to use the passed-in number of task nodes for the cluster ..
    if int(instance_count) > 0:
        as_minsize, as_maxsize = instance_count, instance_count

    # Attempt to locate the TASK group with the specified name (default name: "TASK")

    try:
        emr_instance_group = get_instance_group_by_name(cluster_id, instance_group)
    except Exception as error:
        logger.error(f"EMR ClusterId:{cluster_id} does not contain a {instance_group} instance group", error)
        error_response.update(
            Message=f"EMR ClusterId:{cluster_id} does not contain a {instance_group} instance group , error: {str(error)}")
        raise exceptions.EMRClusterAutoScalingException(error_response)

    # If we didn't identify any specific user requested auto-scaling parameters, use the current cluster configuration as a guide
    # For minimum use the current task group size, or maximum use the max(current task size, 1)
    if as_minsize == -1 or as_maxsize == -1:
        if 'RequestedInstanceCount' in emr_instance_group.keys():
            as_minsize = emr_instance_group['RequestedInstanceCount']
        elif 'RunningInstanceCount' in emr_instance_group.keys():
            as_minsize = emr_instance_group['RunningInstanceCount']
        else:
            logger.error("Exception occurred while attempting to attach auto-scaling policy")
            error_response.update(Message='Creation of Auto-Scaling policies failed')
            raise exceptions.EMRClusterAutoScalingException(error_response)

    # Prepare the auto-scaling policy based on the configuration template
    autoscaling_template = emr_as_template['AutoScalingPolicy']
    if not autoscaling_template:
        logger.error("Fatal error: Could not locate the Autoscaling policy template in the configuration")
        error_response.update(
            Message='Fatal error: Could not locate the Autoscaling policy template in the configuration')
        raise exceptions.EMRClusterAutoScalingException(error_response)

    # Modify the policy
    autoscaling_template['Constraints']['MinCapacity'] = int(as_minsize)
    autoscaling_template['Constraints']['MaxCapacity'] = int(as_maxsize)

    try:
        response = add_emr_auto_scaling(cluster_id, emr_instance_group, autoscaling_template)
    except Exception as error:
        logger.error("Exception occurred while attempting to attach auto-scaling policy ...exiting", error)
        error_response.update(Message='Auto-Scaling policies attachment failed')
        raise exceptions.EMRClusterAutoScalingException(error_response)

    return {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_id": cluster_id,
        'status': json.dumps(response['AutoScalingPolicy']['Status'])
    }