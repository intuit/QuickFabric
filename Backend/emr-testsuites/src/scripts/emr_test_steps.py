"""
Lambda function to get list of steps executed on emr cluster.
"""

from src.util import exceptions
from src.util.log import setup_logging
from src.util.emrlib import get_emr_steps


def lambda_handler(event, context):
    api_request_id = event.get('api_request_id')
    logger = setup_logging(api_request_id, context.aws_request_id)
    cluster_id = event.get('cluster_id')

    success_response = {
        'statusCode': 201,
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        'cluster_id': cluster_id,
        'steps_count': 0
    }

    error_response = {
        "statusCode": 500,
        "errorType": "NoClusterFound",
        "errorMessage": "Unable to fetch cluster step details."
    }

    logger.info("Getting steps by cluster_id")
    try:
        response = get_emr_steps(cluster_id)
    except Exception as e:
        logger.error(e)
        raise exceptions.EMRTestRunException(error_response)
    else:
        success_response.update(steps_count=len(response))
        success_response.update(steps=response)

    return success_response
