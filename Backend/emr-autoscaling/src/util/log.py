"""
Base script for logging Lambda functions
"""

import logging
import sys


def setup_logging(api_request_id, lambda_request_id):
    logger = logging.getLogger()
    for handler in logger.handlers:
        logger.removeHandler(handler)

    handler = logging.StreamHandler(sys.stdout)

    log_format = f"[%(asctime)s] [api_request_id={api_request_id} lambda_request_id={lambda_request_id}] %(levelname)s: %(message)s"
    handler.setFormatter(logging.Formatter(log_format, "%Y-%m-%d %H:%M:%S"))
    logger.addHandler(handler)
    logger.setLevel(logging.INFO)

    return logger
