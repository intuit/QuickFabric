"""
EMR cluster existence pre-check before triggering cluster creation
"""

import json

from src.util.emrlib import get_cluster_id
from src.util.log import setup_logging
from src.util.exceptions import ClusterPreCheckException, MissingEMRCluster
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    api_request_id = event.get('api_request_id', 'null')

    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name = event.get('cluster_name', None)
    cluster_type = event.get('type', 'unknown').lower()

    if cluster_name is None or cluster_name == '':
        logger.error("Cluster name argument not passed ...exiting")
        # Define error json response for APIs
        error_response = construct_error_response(context, api_request_id)
        error_response.update(status='ClusterPreCheckFailed')
        error_response.update(message='Cluster name argument not passed')
        raise ClusterPreCheckException(error_response)

    # Define successful response
    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "type": cluster_type
    }

    try:
        cluster_id = get_cluster_id(cluster_name)
        logger.info(f"Cluster with id {cluster_id} already exists")
        success_response.update(cluster_id=cluster_id)
        success_response.update(status='ClusterAlreadyExists')
        success_response.update(message='Cluster already exists')
        return success_response
    except MissingEMRCluster as error:
        logger.error(error)
        success_response.update(status='ClusterNotPresent')
        success_response.update(message='Cluster does not exist')
        return success_response
