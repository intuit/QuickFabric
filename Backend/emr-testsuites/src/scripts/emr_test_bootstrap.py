"""
Lambda function to get list of bootstrap actions attached to a emr cluster.
"""

from src.util import exceptions
from src.util.log import setup_logging
from src.util.emrlib import get_bootstrap_actions


def lambda_handler(event, context):
    api_request_id = event.get('api_request_id')
    logger = setup_logging(api_request_id, context.aws_request_id)
    cluster_id = event.get('cluster_id')

    success_response = {
        'statusCode': 201,
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        'cluster_id': cluster_id,
        'count': 0
    }

    error_response = {
        "statusCode": 500,
        "errorType": "NoClusterFound",
        "errorMessage": "Unable to fetch cluster bootstrap information."
    }

    logger.info("Getting bootstraps by cluster_id")
    try:
        response = get_bootstrap_actions(cluster_id)
    except Exception as e:
        logger.error(e)
        raise exceptions.EMRTestRunException(error_response)
    else:
        success_response.update(bootstrap_count=response.get('Count'))
        success_response.update(bootstrap_names=response.get('Names'))

    return success_response
