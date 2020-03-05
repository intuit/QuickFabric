"""
This module has list of custom exception used in differrnt EMR cluster operations
"""

import json


class ClusterPreCheckException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information missing cluster information
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class EMRClusterCreationException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information cluster creation failure information
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class EMRStepValidateException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information cluster creation failure information
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class EMRClusterTerminateException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information cluster creation failure information
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class EMRCostUsageException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information cluster creation failure information
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class EMRClusterTerminationProtectionException(Exception):
    def __init__(self, error):
        """
        :param error: Information about termination protection error.
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class MissingEMRCluster(Exception):
    """
    Raise a exception for cluster name missing in the account
    """
    def __init__(self, cluster_name):
        """
        :param cluster_name: EMR Cluster name
        :type cluster_name: string
        """
        self.cluster_name = cluster_name
        return

    def __str__(self):
        self.error = f"The EMR cluster {self.cluster_name} does not exist."
        return self.error


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


class UnknownStepTypeError(Exception):
    """
    The provided step type is not supported.
    :ivar step_type: the step_type provided.
    """
    def __init__(self, step_type=None):
        self.step_type = step_type
        return

    def __str__(self):
        error = f'aws: error: The step type {self.step_type} is not supported.'
        return error


class EMRClusterAddStepException(Exception):
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


class EMRClusterValidateStepException(Exception):
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


class ClusterStatusCheckException(Exception):
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


class EMRClusterValidationException(Exception):
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


class EMRDNSOperationsException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information dns operation failure information
        :type error: Dict
        """
        self.error = error
        return

    def __str__(self):
        self.error = json.dumps(self.error)
        return self.error


class EMRRMProxyException(Exception):
    def __init__(self, error):
        """
        :param error: Dict object contain information for RM call failure information
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


class MissingConstructorParameter(Exception):
    def __init__(self, classname=None, parameter=None):
        self.classname = classname
        self.parameter = parameter
        return

    def __str__(self):
        self.error = "The required \"{}\" parameter is missing from the {} constructor.".format(
            self.parameter, self.classname)
        return self.error


class MissingDynamoDBTable(Exception):
    def __init__(self, classname=None, parameter=None):
        self.classname = classname
        self.parameter = parameter
        return

    def __str__(self):
        self.error = "The DynamoDB table \"{}\" does not exist.".format(
            self.parameter, self.classname)
        return self.error