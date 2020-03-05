"""
Lambda function to list auto-scaling policies to EMR cluster
"""

import json

from src.util.log import setup_logging
from src.util import exceptions
from src.util.emrlib import get_instance_group_by_name


def lambda_handler(event, context):
    api_request_id = event.get('api_request_id', 'null')
    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_id = event.get('cluster_id')
    instance_group = event.get('instance_group') or "TASK"

    # Define error json response for APIs
    error_response = {
        "statusCode": 500,
        "lambda_function_name": context.function_name,
        "log_group_name": context.log_group_name,
        "log_stream_name": context.log_stream_name,
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id
    }

    try:
        emr_instance_group = get_instance_group_by_name(cluster_id, instance_group)
    except Exception as error:
        logger.error(f"EMR ClusterId:{cluster_id} does not contain a {instance_group} instance group", error)
        error_response.update(
            Message=f"EMR ClusterId:{cluster_id} does not contain a {instance_group} instance group , error: {str(error)}")
        raise exceptions.EMRClusterAutoScalingException(error_response)

    asg_policy = emr_instance_group.get('AutoScalingPolicy', {})

    status = {
        'state': asg_policy.get('Status', {}).get('State', 'DETACHED'),
        'min': asg_policy.get('Constraints', {}).get('MinCapacity', 0),
        'max': asg_policy.get('Constraints', {}).get('MaxCapacity', 0),
        'rules': len(asg_policy.get('Rules', []))
    }

    return {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_id": cluster_id,
        'status': json.dumps(status)
    }