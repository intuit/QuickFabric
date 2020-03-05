"""
Lambda function to run self test on emr cluster
"""

from random import choice
import string

from src.util.log import setup_logging
from src.util.emrlib import emr_add_step, emr_validate_step

from src.util import exceptions
from src.util import constants


def lambda_handler(event, context):
    # Fetch API requestId if triggered via API g/w
    api_request_id = event.get('api_request_id', 'null')

    logger = setup_logging(api_request_id, context.aws_request_id)

    cluster_name = event.get('cluster_name', None)
    cluster_id = event.get('cluster_id', None)
    app = event.get('app', 'hive').lower()
    cluster_type = event.get('cluster_type', 'unknown').lower()

    # Define error json response for APIs
    error_response = {
        "statusCode": 500,
        "cluster_type": cluster_type,
        "lambda_function_name": context.function_name,
        "log_group_name": context.log_group_name,
        "log_stream_name": context.log_stream_name,
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id
    }

    if app not in ['spark', 'hive', 'customjar']:
        logger.error(f"Unsupported app type {app}")
        error_response.update(Message=f'Unsupported app type {app}')
        raise exceptions.EMRTestRunException(error_response)

    step_config_list = []
    step_config = {}

    if app == 'spark':
        step_config['Name'] = constants.DEFAULT_TEST_SPARK_STEP_NAME
        step_config['ActionOnFailure'] = constants.DEFAULT_FAILURE_ACTION
        args_list = [constants.SPARK_SUBMIT_COMMAND]
        args_list += constants.DEFAULT_TEST_SPARK_ARGS
        jar_config = {'Jar': constants.SCRIPT_RUNNER_JAR, 'Args': args_list}
        step_config['HadoopJarStep'] = jar_config

    elif app == 'hive':
        step_config['Name'] = constants.DEFAULT_TEST_HIVE_STEP_NAME
        step_config['ActionOnFailure'] = constants.DEFAULT_FAILURE_ACTION
        args_list = [constants.HIVE_SCRIPT_COMMAND, constants.RUN_HIVE_SCRIPT, constants.ARGS]
        args_list += constants.DEFAULT_TEST_HIVE_ARGS
        jar_config = {'Jar': constants.COMMAND_RUNNER, 'Args': args_list}
        step_config['HadoopJarStep'] = jar_config

    elif app == 'customjar':
        step_config['Name'] = constants.DEFAULT_TEST_CUSTOM_JAR_STEP_NAME
        step_config['ActionOnFailure'] = constants.DEFAULT_FAILURE_ACTION
        # Step might fail same output dir is used, generate random output directory

        dir_suffix = ''.join(choice(string.ascii_lowercase + string.digits) for _ in range(5))
        output_dir = constants.CUSTOM_JAT_TEST_ARGS_OUTPUT_PREFIX + dir_suffix

        args_list = constants.CUSTOM_JAR_TEST_ARGS
        args_list.append(output_dir)
        jar_config = {'Jar': constants.CUSTOM_TEST_JAR, 'Args': args_list}
        step_config['HadoopJarStep'] = jar_config

    step_config_list = [step_config]

    try:
        step_id = emr_add_step(cluster_id, step_config_list)
    except Exception as error:
        logger.error(f"Unable to test app  {app} on emr cluster {cluster_id}", error)
        error_response.update(Message='EMR test app failed')
        raise exceptions.EMRTestRunException(error_response)
    else:
        validation_status = emr_validate_step(cluster_id, step_id)

    return {
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id,
        "cluster_name": cluster_name,
        "app": app,
        "cluster_id": cluster_id,
        "step_id": validation_status.get('step_id'),
        "status": validation_status.get('status'),
        "message": validation_status.get('message')
    }