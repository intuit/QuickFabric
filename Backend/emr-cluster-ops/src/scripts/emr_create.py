"""
Lambda function to launch EMR cluster using Boto3 SDK
"""

from src.scripts.emr_create_nonkerb import lambda_handler as emr_nonkerb
from src.util.commlib import construct_error_response
from src.util import  exceptions


def lambda_handler(event, context):
    cluster_type = event.get('sub_type', 'unknown').lower()
    if cluster_type in {"nonkerb"}:
        return emr_nonkerb(event, context)
    else:
        # Throw an exception for unsupported cluster type passed
        api_request_id = event.get('api_request_id')
        error_response = construct_error_response(context, api_request_id)
        raise exceptions.EMRClusterCreationException(error_response)