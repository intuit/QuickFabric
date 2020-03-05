#!/usr/bin/python
# script to install serverless plugins

import subprocess
import json
import os


def install_serverless_module(plugin_name, version='latest'):
    """
    Installs a serverless plugin in current directory
    :param plugin_name: name of the plugin to be installed
    :type plugin_name: string
    :param version: Version number of the plugin
    :type version:
    :return: None
    """

    print(f"Installing serverless plugin {plugin_name} version @{version}")

    command = f"set -o pipefail; npm install {plugin_name}@{version} 2> /dev/null"
    process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)

    process.wait()

    if process.returncode != 0:
        print(f"Serverless plugin {plugin_name} exited with return code {process.returncode}")
        exit(1)


if __name__ == '__main__':
    cwd = os.path.dirname(__file__)
    plugin_metadata = 'plugin_metadata.json'
    metadata_file = os.path.join(cwd, plugin_metadata)

    try:
        with open(metadata_file) as f:
            metadata = json.load(f)
    except Exception as error:
        print("Unable to parse metadata json: ", error)
        exit(1)

    print("#" * 52)
    print("#" * 10, "Installing Serverless Modules ", "#" * 10)
    print("#" * 10, " " * 30, "#" * 10)
    print("#" * 52)

    for plugin in metadata:
        # instantiating process with arguments
        install_serverless_module(plugin, metadata[plugin])