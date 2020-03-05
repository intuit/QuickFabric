#!/usr/bin/python
import sys
import yaml
import os
import boto3
import botocore.exceptions
from subprocess import Popen, PIPE, CalledProcessError
import argparse
import tempfile
import shutil

from logger import setup_logger


cwd = os.path.abspath(__file__)
deploy_dir =  os.path.dirname(cwd)

os.environ["PYTHONUNBUFFERED"] = "1" # run in unbuffered mode to view realtime output

logger = setup_logger()


def is_docker_env():
    if os.path.isfile('/.dockerenv'):
        log_info('Running deployment in docker container..')
        return True
    return False


def create_temp_dir():
    temp_dir = tempfile.mkdtemp(prefix="node_modules")
    return temp_dir


def get_account_id():
    sts = boto3.client('sts')
    account_id = None

    try:
        response = sts.get_caller_identity()
        account_id = response.get('Account')

    except Exception as exception:
        log_error(exception)
        raise botocore.exceptions.ClientError

    return account_id


def log_info(message):
    logger.info(message)


def log_error(message):
    logger.error(message)


def generate_config_file(account_id):
    deploy_config = os.path.join(deploy_dir, 'deploy.yml')
    stream = open(deploy_config, 'r')
    yml = yaml.safe_load(stream)
    config_file =  os.path.join(deploy_dir, 'config.yml')
    if os.path.exists(config_file):
        os.remove(config_file)
    with open(config_file, "w") as configfile:
        try:
            config_dump = yml[account_id]
            config_dump['account-id'] = account_id
            yaml.dump(config_dump, configfile, default_flow_style=False)
            log_info(f"Configuration for account number {account_id} saved to {config_file}.")
        except KeyError as error:
            log_error(error)
            log_error("Configuration for account number %s not found." % account_id)
            exit(1)


def read_config():
    config_file = os.path.join(deploy_dir, 'config.yml')
    stream = open(config_file, 'r')
    yml = yaml.safe_load(stream)
    return yml


def run(command):
    log_info("Running command %s" % command)
    command = command.split()

    process = Popen(command, stdout=PIPE, universal_newlines=True )
    while True:
        line = process.stdout.readline()
        log_info(line.strip())
        exit_code = process.poll()
        if exit_code is None:
            pass
        elif exit_code > 0:
            log_error("%s command execution failed, with exit code %d" % (command, exit_code))
            return 1
        if line == '' and process.poll() is not None:
            return 0


def clean_up(fs_object):
    if fs_object is None:
        log_info("Not a file system object.")
    elif os.path.islink(fs_object):
        os.unlink(fs_object)
    elif os.path.isfile(fs_object):
        os.remove(fs_object)
    elif os.path.isdir(fs_object):
        shutil.rmtree(fs_object, ignore_errors=True)
    else:
        log_error("Not a valid file system type")


def install_serverless_plugins(dir_name):
    log_info(f"Installing node module into temp dir {dir_name}")
    os.chdir(dir_name)
    # install serverless plugins
    plugin_script = f'{deploy_dir}/plugin_install.py'
    plugin_install_cmd = f'python3 {plugin_script}'

    exit_status = run(plugin_install_cmd)
    if exit_status > 0:
        logger.error(f"{plugin_install_cmd} command execution failed")
        sys.exit(1)

def install_remove_module(action, module, temp_dir):
    """
    Installs a serverless project
    :param action: str, install/remove a serverless module
    :param module: str, serverless module to be installed/removed
    :param temp_dir: os.path, an temp os path to install node modules
    :return: None
    """
    heir_dir = os.path.dirname(deploy_dir)
    module_dir = os.path.join(heir_dir, module )

    if os.path.exists(module_dir):
        log_info(f"Module found ..{module} in path {module_dir}")
    else:
        log_error(f'Module {module} does not exist, in path {module_dir}')
        log_error(f'{action} failed..')
        exit(1)

    # Perform sls deploy
    os.chdir(module_dir)
    log_info("current directory %s" % module_dir)
    cwd = os.getcwd()
    log_info("Directory changed successfully %s" % cwd)

    if not is_docker_env():
        node_module_dir = os.path.join(module_dir, 'node_modules')
        if os.path.islink(node_module_dir):
            clean_up(node_module_dir)
        os.symlink(os.path.join(temp_dir, 'node_modules'), node_module_dir, target_is_directory=True)
    else:
        node_module_dir = None

    log_info("#" * 55)
    log_info("#" * 12 + f" {action} module {module} " + "#" * 12)
    log_info("#" * 55)

    sls_cmd = f'serverless {action} --force --conceal'
    exit_status = run(sls_cmd)
    clean_up(node_module_dir)

    if exit_status > 0:
        logger.error(f"{sls_cmd} command execution failed")
        sys.exit(1)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(prog='PROG', usage='%(prog)s [options]')
    parser.add_argument('action', nargs="?" , default='deploy', type=str, choices=['deploy', 'remove'], help='Deploy/remove a Serverless service')
    args = parser.parse_args()
    action = args.action

    try:
        account_id = get_account_id()
    except Exception as error:
        log_error("Unable to fetch account info, exiting..." )
        logger.error(error)
        exit(1)
    log_info(f'AWS Account ID {account_id}')

    # Install node modules
    if is_docker_env():
        install_dir = '/'
        install_serverless_plugins(install_dir)
    else:
        install_dir = create_temp_dir()
        install_serverless_plugins(install_dir)

    # Extract config from deploy.yml
    generate_config_file(str(account_id))

    config = read_config()

    if action == 'deploy':
        # Install emr-cluster-ops which is key module for emr cluster management
        install_remove_module(action, 'emr-cluster-ops', install_dir)

        # Read the config from config.yml

        if 'plugins' in config:
            for plugin in config['plugins']:
                install_remove_module(action, plugin, install_dir)

        if not is_docker_env():
            clean_up(install_dir)
        config_file =  os.path.join(deploy_dir, 'config.yml')
        clean_up(config_file)
        log_info("Deployment completed successfully..")

    elif action == 'remove':
        if 'plugins' in config:
            for plugin in config['plugins']:
                install_remove_module(action, plugin, install_dir)
        install_remove_module(action, 'emr-cluster-ops', install_dir)

        if not is_docker_env():
            clean_up(install_dir)
        config_file = os.path.join(deploy_dir, 'config.yml')
        clean_up(config_file)
        log_info("Removal completed successfully..")
