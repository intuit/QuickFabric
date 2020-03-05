"""
    Lambda function to terminate an EMR cluster
"""

from src.util.log import setup_logging
from src.util.emrlib import terminate_emr_cluster
from src.util import exceptions
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id', 'null')
    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name = event.get('cluster_name', None)
    cluster_id = event.get('cluster_id', None)

    force = event.get('force', False)

    try:
        terminate_emr_cluster(cluster_name, cluster_id, force)
    except Exception as error:
        # Define error json response for APIs
        error_response = construct_error_response(context, api_request_id)
        error_response.update(status='TERMINATION_INITIATION_FAILED')
        error_response.update(message=str(error))
        logger.error(f"Unable to terminate EMR cluster:{cluster_name} ...exiting \n error: {error}")
        raise exceptions.EMRClusterTerminateException(error_response)

    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "cluster_id": cluster_id,
        "status": "TERMINATION_INITIATED",
        "message": "EMR termination initiated successfully"
    }

    return success_response
