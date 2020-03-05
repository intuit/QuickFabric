"""
Lambda function to validate status of EMR cluster post launch
"""

import json

from src.util.log import setup_logging
from src.util.emrlib import get_emr_cluster_status, get_cluster_id, get_cluster_metadata
from src.util.emrlib import get_cluster_name, delete_security_group, empty_sg_rules, get_network_interface_association
from src.util import constants
from src.util import exceptions
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id', 'null')
    cluster_id = event.get('cluster_id')

    logger = setup_logging(api_request_id, context.aws_request_id)

    logger.info(f"Validating EMR cluster {cluster_id}")

    # Define generic response for Lambda fns & APIs
    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_id": cluster_id,
    }

    try:
        cluster_name = get_cluster_name(cluster_id, terminated=True)
    except Exception as error:
        logger.info("Cluster does not exist.")
        # Define error json response for APIs
        error_response = construct_error_response(context, api_request_id)
        error_response.update(status='ClusterNotPresent')
        error_response.update(message=f'Cluster ID {cluster_id} does not exist')
        raise exceptions.EMRClusterValidationException(error_response)
    else:
        success_response.update(cluster_name=cluster_name)

    response = get_emr_cluster_status(cluster_id, detail=True)

    if response.get('status').upper() in ['WAITING', 'RUNNING']:
        logger.info("EMR cluster is up and running..")
        success_response.update(status=response.get('status').upper())

        # Fetching RM url and master IP as cluster has been created successfully
        cluster_metadata = get_cluster_metadata(cluster_id)
        success_response.update(rm_url=cluster_metadata.get('rm_url'))
        success_response.update(master_ip=cluster_metadata.get('master_ip'))

    elif response.get('status').upper() in constants.LIST_CLUSTERS_PROVISION_STATES:
        logger.info("EMR cluster creation inprogress...")
        success_response.update(status=response.get('status').upper())

    else:
        logger.info(f"EMR cluster failed with {response.get('status')} error \n message: {response.get('message')}")
        success_response.update(status="FAILED")

        try:
            # Get the security group from EMR cluster ec2 attributes
            master_sg_id = response.get('ec2_attributes').get('master_sg')
            if master_sg_id:
                interface_status = get_network_interface_association(master_sg_id)

                if interface_status:
                    logger.info("Security ID is attached to an interface, skipping the deletion..")
                else:
                    # Remove all the rules for security group, before deleting
                    empty_sg_rules(master_sg_id)
                    # Delete the master and service security group of the emr if cluster is in terminated state
                    delete_security_group(master_sg_id)

            service_sg_id = response.get('ec2_attributes').get('service_sg')
            if service_sg_id:
                interface_status = get_network_interface_association(service_sg_id)

                if interface_status:
                    logger.info("Security ID is attached to an interface, skipping the deletion..")
                else:
                    # Remove all the rules for security group, before deleting
                    empty_sg_rules(service_sg_id)
                    # Delete the master and service security group of the emr if cluster is in terminated state
                    delete_security_group(service_sg_id)

        except Exception as error:
            logger.error(error)

    return success_response
