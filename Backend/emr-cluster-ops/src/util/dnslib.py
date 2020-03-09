"""
This module implements the common functions required for DNS flip operations
"""

import botocore.exceptions
from boto3 import Session

from src.util.log import setup_logging

logger = setup_logging('dnslib', '')


def get_dns_records(record, zone_name):
    """
    Returns only the records of given cluster record.
    Args:
        record (dns name), zone_name (route 53 zone)

    Returns:
        boolean (true or false), record domain
    """
    session = Session()
    route53 = session.client('route53')

    zone_id = get_dns_hostedid(zone_name)

    logger.info("Retrieved Zone ID: ", zone_id)

    if zone_id is None:
        logger.info("Error: Unable to fetch the Hosted zone id of the given zone name: " + zone_name)
        return None, {"Description": "Unable to fetch the details of the given zone : " + zone_name}

    try:
        response = route53.list_resource_record_sets(HostedZoneId=zone_id, StartRecordName=zone_name)
        record_domain = [rec for rec in response.get('ResourceRecordSets') if record == rec.get('Name')]

    except Exception as e:
        logger.error("Error: Unable to fetch the details of the given zone " + zone_id)
        logger.exception(e)
        raise botocore.exceptions.ClientError(e, 'get_dns_records')

    return True, record_domain


def get_dns_hostedid(zone_name):
    """
    Recurse through all the hosted zones until it gets the
    HostID of the :param domain.
    NB: Format of hostedid from the json response: 'Id': '/hostedzone/Z6PYZVGFYKJ74
    :param zone_name: Route 53 Zone in which DNS exists
    :type zone_name: string
    :return: Zone id
    :rtype: string
    """
    session = Session()
    route53 = session.client('route53')

    # change to emr - sbg.a.intuit.com. from emr-sbg.a.intuit.com
    logger.info("Hosted Zone Name : " + zone_name)
    zone_name = zone_name + '.' if not zone_name.endswith('.') else zone_name

    response = route53.list_hosted_zones()

    if len(response.get('HostedZones')) > 0:
        zone_id = [item.get('Id') for item in response.get('HostedZones') if item.get('Name') == zone_name]

        zone_id = zone_id[0].split('/')[-1]
    else:
        logger.error("Error: Error while listing zone name" + zone_name)
        return None, {"Description": "Error while listing zone name" + zone_name}

    return zone_id


def dns_deupsert(action, cluster_name, record, ip_address, zone_id):
    """
    deupset stands for (De - delete, up - update, sert - Insert)
    Function perform delete, update, insert entry to Route53 hosted Zone.
    Args:
        action, cluster_name, record (dns name), ip_address (master_ip), zone_id

    Returns:
        record (dns name), message for success or failure
    """

    session = Session()
    route53 = session.client('route53')

    action = action.upper()
    if action == 'UPDATE':
        action = 'UPSERT'

    dnschangebatch = {
        'Comment': 'Master IP of EMR cluster ' + cluster_name,
        'Changes': [
            {
                'Action': action,
                'ResourceRecordSet': {
                    'Name': record,
                    'Type': 'A',
                    'TTL': 60,
                    'ResourceRecords': [
                        {
                            'Value': ip_address
                        },
                    ]
                }}]}

    try:
        print("Change Batch Request Payload: ", dnschangebatch)
        response = route53.change_resource_record_sets(HostedZoneId=zone_id, ChangeBatch=dnschangebatch)
        id = response.get('ChangeInfo').get('Id')
        print('Change request ID ', id)
    except Exception as e:
        logger.error("Error: Unable to %s record set" % action.lower())
        logger.error(e)
        raise botocore.exceptions.ClientError(e, 'dns_deupsert')

    return record, "Record %s successful." % action.lower()