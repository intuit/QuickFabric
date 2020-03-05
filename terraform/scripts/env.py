import os
import json

data = {}


def get_os_env(variable):
	return  os.environ.get(variable.upper(),os.environ.get(variable.lower(),""))

data["HOME"] = get_os_env('HOME')
data["AWS_PROFILE"] = get_os_env('AWS_PROFILE')
data["AWS_ACCESS_KEY_ID"] = get_os_env('AWS_ACCESS_KEY_ID')
data["AWS_SECRET_ACCESS_KEY"] = get_os_env('AWS_SECRET_ACCESS_KEY')
data["AWS_SESSION_TOKEN"] = get_os_env('AWS_SESSION_TOKEN')

print(json.dumps(data))

