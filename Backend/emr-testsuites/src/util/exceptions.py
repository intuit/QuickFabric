"""
This module has list of custom exception used in different EMR cluster operations
"""

import json


class MissingParametersError(Exception):

    """
    One or more required parameters were not supplied.
    :ivar object_name: The object that has missing parameters.
        This can be an operation or a parameter (in the
        case of inner params).  The str() of this object
        will be used so it doesn't need to implement anything
        other than str().
    :ivar missing: The names of the missing parameters.
    """
    def __init__(self, object_name=None, missing=None):
        self.object_name = object_name
        self.missing = missing
        return

    def __str__(self):
        error = f'aws: error: The following required parameters are missing for {self.object_name}: {self.missing}.'
        return error


class EMRTestRunException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information missing cluster information and error
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class FileReadError(Exception):
    def __init__(self, path=None, message=None):
        self.path = path
        self.message = message
        return

    def __str__(self):
        self.error = f"An error occurred while reading the file \"{self.path}\": {self.message}"
        return self.error