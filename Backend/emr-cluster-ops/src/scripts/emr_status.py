""" Lambda function to check the status of cluster
"""
import json

from src.util.emrlib import get_emr_cluster_status, get_cluster_name, delete_security_group, empty_sg_rules, get_network_interface_association
from src.util.log import setup_logging
from src.util import exceptions
from src.util import constants
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id', 'null')

    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_id = event.get('cluster_id', None)

    # Define error json response for APIs
    error_response = construct_error_response(context, api_request_id)

    if cluster_id is None:
        logger.error("ClusterId argument not passed ...exiting")
        error_response.update(status='ClusterStatusCheckFailed')
        error_response.update(message='ClusterId argument not passed')
        raise exceptions.ClusterStatusCheckException(json.dumps(error_response))

    try:
        response = get_emr_cluster_status(cluster_id, detail=True)
    except Exception as error:
        logger.error("An error occurred ...exiting \n" + str(error))
        error_response.update(status='ClusterStatusCheckFailed')
        error_response.update(message=f'{str(error)}')
        raise exceptions.ClusterStatusCheckException(error_response)
    else:
        success_response = {
            "api_request_id": api_request_id,
            "lambda_request_id": context.aws_request_id,
            "cluster_name": response.get('cluster_name'),
            "cluster_id": response.get('cluster_id'),
            "status": response.get('status'),
            "message": response.get('message')
        }
    try:
        if response.get('status') in constants.TERMINATED_STATES:
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

    logger.info(f"ClusterName: {response.get('cluster_name')}  ClusterId: {response.get('cluster_id')}:  Status: {response.get('status')}")
    return success_response
