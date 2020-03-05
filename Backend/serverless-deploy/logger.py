import logging
import sys


def setup_logger(debug=False):
    """
    Configure logger
    :return: logger handler
    """

    level = logging.DEBUG if debug else logging.INFO
    logger = logging.getLogger("Quickfabric Deploy")
    logger.handlers = []
    logger.propagate = False
    handler = logging.StreamHandler(sys.stdout)
    formatter = logging.Formatter("%(asctime)s  - %(name)s  %(levelname)s [%(filename)s:%(lineno)d] %(message)s")
    handler.setFormatter(formatter)
    logger.addHandler(handler)

    logger.setLevel(level)

    return logger
