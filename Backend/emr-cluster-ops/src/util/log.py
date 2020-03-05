"""
Base script for logging Lambda functions
"""

import logging
import sys
import os
import json
from botocore.vendored import requests


def setup_logging(api_request_id, lambda_request_id):
    logger = logging.getLogger()
    for handler in logger.handlers:
        logger.removeHandler(handler)
    logger.handlers = []

    handler = logging.StreamHandler(sys.stdout)

    log_format = f"[%(asctime)s] [api_request_id={api_request_id} lambda_request_id={lambda_request_id}] %(levelname)s: %(message)s"
    handler.setFormatter(logging.Formatter(log_format, "%Y-%m-%d %H:%M:%S"))
    logger.propagate = False
    logger.addHandler(handler)
    logger.setLevel(logging.INFO)

    return logger


def slack_notification(event):
    path = os.path.dirname(os.path.dirname(__file__))
    notification_file = os.path.join(path, "conf/common/notification.json")
    try:
        slack_url = json.loads(open(notification_file).read())['slack_endpoint']
    except Exception as e:
        raise Exception("File read error, unable to locate file.")

    req_headers = {"Content-Type": "application/json"}
    account = event.get('account')
    severity = event.get('detail', {}).get('severity', 'INFO')
    cluster_id = event.get('detail', {}).get('clusterId', '')
    state = event.get('detail', {}).get('state', 'UNKNOWN')
    cluster_name = event.get('detail').get('name')
    create_by = event.get('detail').get('create_by', '^^')
    slack_post = {
        "attachments": [
            {
                "fallback": "Required plain-text summary of the attachment.",
                "color": "#36a64f",
                "pretext": "Amazon EMR cluster id %s with name %s is %s" % (cluster_id, cluster_name, state),
                "title": "Cluster Name %s" % cluster_name,
                "text": "Account ID %s" % account,
                "fields": [
                    {
                        "title": "clusterId",
                        "value": cluster_id,
                        "short": True
                    },
                    {
                        "title": "severity",
                        "value": severity,
                        "short": True
                     },
                    {
                        "title": "state",
                        "value": state,
                        "short": True
                     },
                    {
                        "title": "created_by",
                        "value": create_by,
                        "short": True
                    }
                ],
                "thumb_url": "http://example.com/path/to/thumb.png",
                "footer": "EMR Notification",
                "footer_icon": "https://platform.slack-edge.com/img/default_application_icon.png",
            }
        ]
    }
    response = requests.post(slack_url, json=slack_post, headers=req_headers)
    print("Slack notification status : %s status code %s " % (response.content, response.status_code))