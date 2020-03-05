# coding=utf-8
from __future__ import print_function

import json
from src.util.dnslib import get_dns_records, get_dns_hostedid, dns_deupsert
from src.util.log import setup_logging
from src.util import exceptions
from src.util.commlib import construct_error_response

def lambda_handler(event, context):
    """
    Performs DNS flip based on dns_name and cluster_name
    :param event:
    :param context:
    :return:
    """
    api_request_id = event.get('api_request_id', 'null')
    account = event.get('account')  # AWS Account number
    action = event.get('action').lower()  # DNS Action [ create, delete, update ]
    record = event.get('dns_name').lower()  # DNS record name
    cluster_name = event.get('cluster_name')  # EMR cluster name
    master_ip = event.get('master_ip')  # IP of Master Node of Cluster

    logger = setup_logging(api_request_id, context.aws_request_id)

    success_response = {
        "statusCode": 200,
        "status": "Completed",
        "message": "DNS record changed successfully."
    }

    error_response = construct_error_response(context, api_request_id)
    error_response.update(message="Unable to update DNS record.")

    # Fetch the DNS name and retrieve hosted zone out of it, if DNS name is not provided throw an exception
    if event.get('dns_name'):
        hosted_zone = record.split(".", 1)[1]
        pass
    else:
        error_response.update(message='DNS Name is not passed for DNS Flip')
        raise exceptions.EMRDNSOperationsException(error_response)

    # Checking if non-empty Master IP is passed
    if not master_ip:
        error_response.update(message="Empty Master IP is passed for DNS Operation. Exiting.")
        raise exceptions.EMRDNSOperationsException(error_response)

    logger.info(record)
    logger.info(master_ip)
    logger.info(cluster_name)
    logger.info(hosted_zone)

    # Fetching Hosted Zone ID
    zone_id = get_dns_hostedid(hosted_zone)

    if zone_id is None:
        logger.error("Unable to fetch the Hosted zone id of the given zone name :" + hosted_zone)
        error_response.update(message='Unable to fetch the Hosted zone id of the given zone name.')
        raise exceptions.EMRDNSOperationsException(error_response)

    # Fetching DNS record for Hosted Zone
    record_exist, record_response = get_dns_records(record, hosted_zone)

    if action == "create":
        if record_response:
            logger.error("record already exist.. %s" % record_response)
            error_response.update(message='Record Already Exist. It can not be created again.')
            raise exceptions.EMRDNSOperationsException(error_response)
        else:
            dns_name, message = dns_deupsert(action, cluster_name, record, master_ip, zone_id)
            if dns_name:
                success_response['message'] = message
                success_response['dnsName'] = dns_name
                logger.info(success_response)
                return json.dumps(success_response)
            else:
                error_response['message'] = message
                logger.error(error_response)
                raise exceptions.EMRDNSOperationsException(error_response)

    elif action == "update":
        if record_response:
            dns_name, message = dns_deupsert(action, cluster_name, record, master_ip, zone_id)
            if record_exist:
                success_response['message'] = message
                success_response['dnsName'] = dns_name
                logger.info(success_response)
                return json.dumps(success_response)
            else:
                error_response['message'] = message
                logger.error(error_response)
                raise exceptions.EMRDNSOperationsException(error_response)

        else:
            logger.error("Unable to update record. It does not exist.. %s" % record_response)
            error_response.update(message='Unable to update record. Record does not exist.')
            raise exceptions.EMRDNSOperationsException(error_response)

    elif action == "delete":
        if record_response:
            logger.info("record already exist.. %s" % record_response)
            dns_name, message = dns_deupsert(action, cluster_name, record, master_ip, zone_id)
            if record_exist:
                success_response['message'] = message
                success_response['dnsName'] = dns_name
                logger.info(success_response)
                return json.dumps(success_response)
            else:
                error_response['message'] = message
                logger.error(error_response)
                raise exceptions.EMRDNSOperationsException(error_response)

        else:
            logger.error("Unable to delete record %s DNS entry does not exist." % record)
            error_response.update(message='Unable to delete record %s DNS entry does not exist.' % record)
            raise exceptions.EMRDNSOperationsException(error_response)

