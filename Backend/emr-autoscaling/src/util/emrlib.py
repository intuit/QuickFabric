"""
This module implements the common functions required for EMR cluster operations
"""

import botocore.exceptions
from boto3 import Session

from src.util.log import setup_logging

logger = setup_logging('emrlib', '')


def get_instance_group_by_name(cluster_id: str, task_group_name: str):
    """
        Retrieve the group id of a task instance group based on its name

        :param cluster_id: The ClusterId
        :param task_group_name: The name of the task instance group
        :return: The instance group JSON object or None (if not found)
        """
    session = Session()
    emr = session.client('emr')
    try:
        response = emr.list_instance_groups(ClusterId=cluster_id)
    except Exception as error:
        raise botocore.exceptions.ClientError(error, 'emr_list_instance_groups')

    groups = response.get('InstanceGroups', [])
    for group in groups:
        if group['InstanceGroupType'] == task_group_name.upper():
            return group

    raise botocore.exceptions.ParamValidationError(error=f"EMR Instance group name {task_group_name} not found")


def add_emr_auto_scaling(cluster_id, emr_instance_group, autoscaling_template):
    session = Session()
    emr = session.client('emr')
    try:
        response = emr.put_auto_scaling_policy(ClusterId=cluster_id,
                                               InstanceGroupId=emr_instance_group.get('Id'),
                                               AutoScalingPolicy=autoscaling_template)
    except Exception as error:
        raise botocore.exceptions.ClientError(error, 'put_auto_scaling_policy')
    else:
        return response


def remove_emr_auto_scaling(cluster_id, emr_instance_group):
    session = Session()
    emr = session.client('emr')
    try:
        response = emr.remove_auto_scaling_policy(ClusterId=cluster_id,
                                                  InstanceGroupId=emr_instance_group.get('Id'))
    except Exception as error:
        raise botocore.exceptions.ClientError(error, 'remove_auto_scaling_policy')
    else:
        return response