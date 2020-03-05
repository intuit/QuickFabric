
def construct_error_response(context, api_request_id):
    """
    Construct error response dict
    :param context: AWS Context object, containing properties about the invocation, function, and execution environment.
    :param api_request_id:
    :return: dict, a dict object containing information about the aws loggroup name, stream name and lambda request id
    :rtype:
    """

    error_response = {
        "statusCode": 500,
        "lambda_function_name": context.function_name,
        "log_group_name": context.log_group_name,
        "log_stream_name": context.log_stream_name,
        "api_request_id": api_request_id,
        "lambda_request_id": context.aws_request_id
    }

    return error_response
