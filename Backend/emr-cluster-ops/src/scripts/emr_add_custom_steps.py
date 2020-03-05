""" Lambda function to execute custom step on the fly for an EMR cluster
"""

from src.util.log import setup_logging
from src.util import exceptions
from src.util.emrlib import emr_add_step,emr_validate_step
from src.util.commlib import construct_error_response


def lambda_handler(event, context):
    api_request_id = event.get('api_request_id', 'null')
    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name = event.get('cluster_name')
    cluster_id = event.get('cluster_id')
    step_list = event.get('steps')

    logger.info("Executing EMR add-step")

    # Add custom step
    added_step_list = []

    for step in step_list:
        try:
            step_id_response = emr_add_step(cluster_id, [step])
            emr_validate_response = emr_validate_step(cluster_id, step_id_response)
            added_step_list.append(emr_validate_response)
        except Exception as error:
            logger.error(f"Unable to add custom step {step}")
            # Define error json response for APIs
            error_response = construct_error_response(context, api_request_id)
            error_response.update(message='Unable to add custom step')
            raise exceptions.EMRClusterAddStepException(error_response)

    logger.info("EMR custom add-step response")

    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "cluster_id": cluster_id,
        "steps": added_step_list
    }

    return success_response
