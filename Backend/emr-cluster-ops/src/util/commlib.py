import os
import datetime


def check_file_exist(path):
    """
    :param path: absolute path to file
    :type path: file
    :return: True.False based on file existence
    :rtype: bool
    """
    file_exist = False
    if os.path.isfile(path):
        file_exist = True

    return file_exist


def get_first_date_of_month():
    """
    Get the first date of month
    Returns:
        first_day_of_month: First Date of Month in isoformat
    """
    first_day_of_month = datetime.date.today().replace(day=1)
    return first_day_of_month.isoformat()


def get_day_month_year():
    """
    Get the todays date
    Returns:
      today: Todays date in isoformat
    """
    today = datetime.date.today()
    return today.isoformat()


def construct_error_response(context, api_request_id):
    """
    Construct error response dict
    :param context: AWS Context object, containing properties about the invocation, function, and execution environment.
    :param api_request_id:
    :return:
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
