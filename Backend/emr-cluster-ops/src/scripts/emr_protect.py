"""
 Lambda function to protect an EMR cluster from accidental termination
"""

from src.util.log import setup_logging
from src.util import exceptions
from src.util.emrlib import get_cluster_id, get_emr_cluster_status, set_emr_termination_protection
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id', 'null')
    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name = event.get('cluster_name')
    cluster_id = event.get('cluster_id')
    termination_protection = event.get('termination_protected', 'status').lower()  # enable/disable/status

    # Define error json response for APIs
    error_response = construct_error_response(context, api_request_id)

    if termination_protection not in {'enable', 'disable', 'status'}:
        logger.error("Invalid action argument. Must be either 'enable', 'disable' or 'status' ...exiting")
        error_response.update(Message='Invalid action argument passed')
        raise exceptions.EMRClusterTerminationProtectionException(error_response)

    # Fetch cluster name, status based on clusterId input
    # check if termination protection is already enabled

    try:
        response_cluster_id = get_cluster_id(cluster_name)
    except Exception as error:
        logger.error(error)
        error_response.update(message=f"Unable to fetch EMR cluster information cluster name {cluster_name}")
        raise exceptions.EMRClusterTerminationProtectionException(error_response)


    if response_cluster_id != cluster_id:
        logger.error(
            f"EMR cluster id:{response_cluster_id} fetched from cluster_name and given cluster_id:{cluster_id} not matching")
        error_response.update(
            message=f"EMR cluster id: {response_cluster_id} fetched from cluster_name and given cluster_id: {cluster_id} not matching")
        raise exceptions.EMRClusterTerminationProtectionException(error_response)

    cluster_status = get_emr_cluster_status(cluster_id)

    if termination_protection == 'enable':
        if cluster_status.get('protection'):
            logger.error(f"Termination protection already enabled for EMR cluster:{cluster_id} @@@")
            error_response.update(message='Termination protection already enabled for EMR cluster')
            raise exceptions.EMRClusterTerminationProtectionException(error_response)

    elif termination_protection == 'disable':
        if not cluster_status.get('protection'):
            logger.error(f"Termination protection already disabled for EMR cluster:{cluster_id}")
            error_response.update(message='Termination protection already disabled for EMR cluster')
            raise exceptions.EMRClusterTerminationProtectionException(error_response)

    elif termination_protection == "status":
        return {
            "api_request_id": api_request_id,
            "lambda_request_id": context.aws_request_id,
            "cluster_name": cluster_name,
            "cluster_id": cluster_id,
            "terminationProtected": 'enabled' if termination_protection else 'disabled'
        }

    set_protection = True if termination_protection == 'enable' else False

    # Set/Remove termination protection
    try:
        set_emr_termination_protection(cluster_id, set_protection)
    except Exception as error:
        logger.error(error)
        logger.error(f"Failed to enable/disable termination protection for clusterId:{cluster_id}")
        error_response.update(message='Failed to enable/disable termination protection')
        error_response.update(terminationProtected='FAILED')
        raise exceptions.EMRClusterTerminationProtectionException(error_response)

    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "cluster_id": cluster_id,
        "terminationProtected": 'enabled' if set_protection else 'disabled'
    }

    return success_response