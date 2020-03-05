"""Lambda function to validate steps added to EMR cluster.
"""

from src.util.log import setup_logging
from src.util import exceptions
from src.util.emrlib import emr_validate_step
from src.util.commlib import construct_error_response

def lambda_handler(event, context):
    api_request_id = event.get('api_request_id', 'null')
    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name= event.get('cluster_name')
    cluster_id = event.get('cluster_id')
    step_ids = event.get('step_ids')

    # Define error json response for APIs
    error_response = construct_error_response(context, api_request_id)

    emr_step_list = []

    for step_id in step_ids:
        try:
            response = emr_validate_step(cluster_id, step_id)
            emr_step_list.append(response)

        except Exception as error:
            logger.error(error)
            logger.error(f"Unable to validate EMR cluster step {step_id}")
            error_response.update(message='Unable to validate EMR cluster step')
            raise exceptions.EMRClusterValidateStepException(error_response)

    success_response = {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "cluster_id": cluster_id,
        "steps": emr_step_list
    }

    return success_response
