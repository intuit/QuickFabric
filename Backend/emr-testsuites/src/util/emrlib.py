"""
This module implements the common functions required for EMR cluster operations
"""

import botocore.exceptions
from boto3 import Session

from src.util.log import setup_logging
from src.util import exceptions

logger = setup_logging('emrlib', '')
session = Session()
emr = session.client('emr')


def get_instance_group_by_name(cluster_id: str, task_group_name: str):
    """
        Retrieve the group id of a task instance group based on its name

        :param cluster_id: The ClusterId
        :param task_group_name: The name of the task instance group
        :return: The instance group JSON object or None (if not found)
        """
    try:
        response = emr.list_instance_groups(ClusterId=cluster_id)
    except Exception as error:
        raise botocore.exceptions.ClientError(error, 'emr_list_instance_groups')

    groups = response.get('InstanceGroups', [])
    for group in groups:
        if group['InstanceGroupType'] == task_group_name.upper():
            return group

    raise botocore.exceptions.ParamValidationError(error=f"EMR Instance group name {task_group_name} not found")


def get_bootstrap_actions(cluster_id: str):
    try:
        response = emr.list_bootstrap_actions(ClusterId=cluster_id)
        bootstrap_actions = response['BootstrapActions']
        bas = []
        for ba in bootstrap_actions:
            bas.append(ba['Name'])

        status = {
            'Count': len(bootstrap_actions),
            'Names': bas
        }
        return status

    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'list_bootstrap_actions')


def get_emr_steps(cluster_id: str):
    step_status_list = []
    try:
        response = emr.list_steps(ClusterId=cluster_id)
        for step in response.get('Steps', []):
            step_status = {
                "step_id" : step.get('Id'),
                "step_name": step.get('Name'),
                "step_status": step.get('Status', {}).get('State')
            }
            step_status_list.append(step_status)

    except Exception as error:
        logger.error(error)
        raise botocore.exceptions.ClientError(error, 'list_steps')

    return step_status_list


def emr_add_step(cluster_id, step_config):
    """
    Adds new steps to a running EMR cluster
    :param cluster_id: A string that uniquely identifies the EMR cluster
    :type cluster_id: string
    :param step_config: List contains emr step config
    :type step_config: List
    :return: step_id: step id added to the EMR cluster
    :rtype: string
    """

    try:
        response = emr.add_job_flow_steps(JobFlowId=cluster_id, Steps=step_config)
    except botocore.exceptions.ClientError as error:
        logger.error("Unable to add step", error)
        raise exceptions.EMRTestRunException(error)
    else:
        step_id = "".join(response.get('StepIds'))

    return step_id


def emr_validate_step(cluster_id, step_id):
    step_status = {}
    try:
        response = emr.describe_step(ClusterId=cluster_id, StepId=step_id)
    except botocore.exceptions.ClientError as error:
        raise exceptions.EMRTestRunException(error)
    else:
        step_status = {
            'step_id': response.get('Step').get('Id'),
            'step_name': response.get('Step').get('Name'),
            'status': response.get('Step').get('Status').get('State'),
            'message': response.get('Step').get('Status', {}).get('FailureDetails', {}).get('Message')
        }
    return step_status
