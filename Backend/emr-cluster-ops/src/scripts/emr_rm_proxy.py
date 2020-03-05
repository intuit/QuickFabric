"""
Lambda function to fetch stats from RM through http call
"""

from src.util.log import setup_logging
from src.util.rmlib import get_metrics_stats, get_apps_stats, get_drelephant_stats
from src.util import exceptions


def lambda_handler(event, context):

    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id')
    metric_type = event.get('metric_type')

    logger = setup_logging(api_request_id, context.aws_request_id)

    error_response = {
        "statusCode": 500,
        "status": "Failed",
        "message": "Unable to call RM Url for fetching metrics."
    }

    if metric_type == "metrics":
        try:
            response = get_metrics_stats(event)
        except Exception as get_metrics_err:
            logger.error("Error getting cluster metrics from RM", get_metrics_err)
            error_response.update(Message="Error getting cluster metrics from RM.")
            raise exceptions.EMRRMProxyException(error_response)

    elif metric_type == "apps":
        try:
            response = get_apps_stats(event)
        except Exception as get_apps_err:
            logger.error("Error getting application metrics from RM", get_apps_err)
            error_response.update(Message="Error getting cluster metrics from RM.")
            raise exceptions.EMRRMProxyException(error_response)

    elif metric_type == "drElephant":
        try:
            response = get_drelephant_stats(event)
        except Exception as get_drelephant_err:
            logger.error("Error getting Dr Elephant results from RM", get_drelephant_err)
            error_response.update(Message="Error getting Dr Elephant results from RM.")
            raise exceptions.EMRRMProxyException(error_response)

    else:
        logger.warn("Improper Type of Metrics Passed. Proper types are: metrics, apps, drElephant", metric_type)
        error_response.update(Message="Improper Type of Metrics Passed. Proper types are: metrics, apps, drElephant")
        raise exceptions.EMRRMProxyException(error_response)

    rm_response = {
        "rm_url": event.get('rm_url'),
        "metric_type": metric_type,
        "metric_stats": response.decode("utf-8"),
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id

    }

    return rm_response
