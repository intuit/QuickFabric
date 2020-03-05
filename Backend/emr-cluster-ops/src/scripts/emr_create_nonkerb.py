"""
Lambda function to launch Nonkerb EMR cluster using boto3 API
"""
import json

from src.util.log import setup_logging
from src.util.log import slack_notification
from src.util.emrlib import create_emr_cluster, get_emr_cluster_status, get_cluster_id
from src.util.exceptions import EMRClusterCreationException
from src.util.commlib import construct_error_response
import os

def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id')
    account = event.get('account')
    created_by = event.get('created_by', 'unknown')
    cluster_type = event.get('sub_type')
    role = event.get('segment', 'testing')
    name = event.get('name')

    # Form cluster name
    if not name or name == "":
        cluster_name = cluster_type + '-' + role
    else:
        cluster_name = name
    event.update({'cluster_name': cluster_name})

    logger = setup_logging(api_request_id, context.aws_request_id)

    # Define error json response for APIs
    error_response = construct_error_response(context, api_request_id)

    # Check if cluster with same name exist?
    cluster_id  = ""
    try:
        cluster_id = get_cluster_id(cluster_name)
    except Exception as error:
        logger.error(error)
        error_response.update(message=str(error))

    if cluster_id.startswith('j-'):
        logger.info(f"Cluster with id {cluster_id} already exists")
        error_response.update(status='ClusterAlreadyExists')
        error_response.update(message=f'Cluster with name {cluster_name} already exists, exiting..')
        raise EMRClusterCreationException(error_response)

    src_dir = os.path.dirname(os.path.dirname(__file__))
    emr_metadata = f'{src_dir}/conf/{account}/emr-metadata.json'
    emr_config_file = f'{src_dir}/conf/{account}/emr-{cluster_type}-config.json'
    user_inputs = event

    try:
        cluster_id = create_emr_cluster(user_inputs, emr_config_file, emr_metadata, role, cluster_type, cluster_name)
    except Exception as error:
        logger.error("Cluster creation failed", error)
        error_response.update(message=str(error))
        raise EMRClusterCreationException(error_response)

    try:
        response = get_emr_cluster_status(cluster_id)
    except Exception as error:
        logger.error("Cluster creation failed", error)
        raise EMRClusterCreationException(error_response)

    dns_record = None

    try:
        r53_zone = json.load(open(emr_metadata)).get('r53_hosted_zone')
    except:
        logger.error("error occured while fetching route53 zone name from metadata file")
    else:
        dns_record = f"{cluster_name}.{r53_zone}"

    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "account": account,
        "segment": role,
        "cluster_name": cluster_name,
        "cluster_type": cluster_type,
        "cluster_id": cluster_id,
        "status": response.get('status'),
        "message": "EMR cluster launch initiated",
        "dns_name": dns_record
    }
    return success_response
