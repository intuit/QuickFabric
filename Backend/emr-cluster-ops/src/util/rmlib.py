"""
This module implements the common functions required for EMR cluster Resource Manager Operations
"""

import botocore.exceptions

from botocore.vendored import requests
from botocore.vendored.requests.exceptions import HTTPError
from src.util.log import setup_logging

logger = setup_logging('rmlib', '')


def get_metrics_stats(event):
    """
    Makes call to provided resource manager Url in event to fetch Cluster Metrics
    :param event: Lambda event containing request params
    :type event: lambda event
    :return: response content
    :rtype: string
    """
    url_suffix = "/ws/v1/cluster/metrics"
    rm_url = event.get('rm_url') + url_suffix

    try:
        response = requests.get(rm_url, timeout=(5, 10))
        print(response)
        response.raise_for_status()
    except HTTPError as http_err:
        logger.error("HTTP error occurred at get_metrics_stats:", http_err)
        raise botocore.exceptions.HTTPClientError(http_err, 'get_metrics_stats')
    except Exception as get_metrics_err:
        logger.error("Exception calling Rm for Metrics Stats", get_metrics_err)
        raise botocore.exceptions.ConnectionError(get_metrics_err, 'get_metrics_stats')

    return response.content


def get_apps_stats(event):
    """
    Makes call to provided resource manager Url in event to fetch Cluster Application Metrics
    based on types of params provided
    :param event: Lambda event containing request params
    :type event: lambda event
    :return: response content
    :rtype: string
    """
    url_suffix = "/ws/v1/cluster/apps"
    rm_url = event.get('rm_url') + url_suffix

    if event.get('final_status') or event.get('states'):
        # Route for getting app stats within a time window and for a particular state
        try:
            response = requests.get(rm_url, timeout=(5, 10), params={
                'finalStatus': event.get('final_status'),
                'states': event.get('states'),
                'finishedTimeBegin': event.get('finished_time_begin'),
                'finishedTimeEnd': event.get('finished_time_end')})
            response.raise_for_status()

        except HTTPError as http_err:
            logger.error("HTTP error occurred at get_apps_stats:", http_err)
            raise botocore.exceptions.HTTPClientError(http_err, 'get_apps_stats')

        except Exception as get_apps_err:
            logger.error("Exception calling Rm for Metrics Stats", get_apps_err)
            raise botocore.exceptions.ConnectionError(get_apps_err, 'get_apps_stats')

        return response.content

    else:
        # Route for getting all apps using Only Time as Param
        try:
            response = requests.get(rm_url, timeout=(5, 10), params={
                'startedTimeBegin': event.get('started_time_begin')
            })

            response.raise_for_status()

        except HTTPError as http_err:
            logger.error("HTTP error occurred at get_apps_stats:", http_err)
            raise botocore.exceptions.HTTPClientError(http_err, 'get_apps_stats')

        except Exception as get_apps_err:
            logger.error("Exception calling Rm for Metrics Stats", get_apps_err)
            raise botocore.exceptions.ConnectionError(get_apps_err, 'get_apps_stats')

        return response.content


def get_drelephant_stats(event):
    """
    Makes call to provided resource manager Url in event to fetch Dr Elephant Metrics
    about applications on the cluster. Dr Elephant needs to be installed on the cluster for this to work.
    :param event: Lambda event containing request params
    :type event: lambda event
    :return: response content
    :rtype: string
    """
    url_suffix = "/rest/search"
    rm_url = event.get('rm_url') + url_suffix

    try:
        response = requests.get(rm_url, timeout=(5, 10), params={
            'finished-time-begin': event.get('finished_time_begin'),
            'finished-time-end': event.get('finished_time_end')
        })
        response.raise_for_status()

    except HTTPError as http_err:
        logger.error("HTTP error occurred at get_drelephant_stats:", http_err)
        raise botocore.exceptions.HTTPClientError(http_err, 'get_drelephant_stats')

    except Exception as get_drelephant_err:
        logger.error("Exception calling RM for Dr Elephant stats", get_drelephant_err)
        raise botocore.exceptions.ConnectionError(get_drelephant_err, 'get_drelephant_stats')

    return response.content
