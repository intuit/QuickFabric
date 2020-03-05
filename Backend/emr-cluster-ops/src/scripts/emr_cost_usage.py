"""
Lambda function to get cost usage details of EMR cluster
"""
from src.util.log import setup_logging
from src.util.commlib import get_first_date_of_month, get_day_month_year
from src.util.emrlib import emr_cost_usage
from src.util import exceptions
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id')
    cluster_name = event.get('cluster_name')

    logger = setup_logging(api_request_id, context.aws_request_id)

    first_date_of_month = get_first_date_of_month()
    today = get_day_month_year()

    try:
        cost_usage = emr_cost_usage(first_date_of_month, today, cluster_name)
    except Exception as error:
        logger.error(f"Unable to fetch cost usage details of EMR Cluster {cluster_name}", error)
        # Define error json response for APIs
        error_response = construct_error_response(context, api_request_id)
        raise exceptions.EMRCostUsageException(error_response)

    api_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "start_date": cost_usage.get('Start_date'),
        "end_date": cost_usage.get('End_date'),
        "total_amount":  cost_usage.get('Total_cost')
    }
    return api_response
