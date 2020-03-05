#!/bin/bash

export BOTO_CONFIG=/dev/null
export AWS_SECRET_ACCESS_KEY=secret
export AWS_ACCESS_KEY_ID=key
export AWS_DEFAULT_REGION=us-west-2

function install_git() {
  echo "Installing git.."
  GIT_CMD=$(which git)
  YUM_CMD=$(which yum)
  APT_GET_CMD=$(which apt-get)
  BREW_CMD=$(which brew)

  if [[ ! -z $GIT_CMD ]]; then
    echo "git is already installed"
    return
  fi

  # Install git on redhat based linux
  if [[ ! -z $YUM_CMD ]]; then
    yum update -y && yum install -y git --enablerepo=* > /dev/null

  # Install git on debian based linux
  elif [[ ! -z $APT_GET_CMD ]]; then
    apt-get update -y && apt-get install -y git > /dev/null

  # Install git on mac osx
  elif [[ ! -z $BREW_CMD ]]; then
    brew install git > /dev/null
  fi
}

function install_requirements() {
  python3 setup.py install > /dev/null
  if [ $? == 0 ]; then
      echo "Requirements installed successfully"
  else
    exit 1
  fi
}

echo "Testing started...."
echo "Installing requirements for running tests"
install_git && install_git

BACKEND_DIR=$(dirname $(readlink -f "$0"))

echo "Backend dir path :: $BACKEND_DIR"

cd "$BACKEND_DIR" || exit
install_requirements

for module in emr-*
do
  cd "$BACKEND_DIR" || exit 1
  echo "Testing module ${module}"
  cd "${module}" || exit
  pytest --cov=tests --cov-config=.coveragerc
  if [ $? == 0 ]; then
    echo "Test completed successfully for module ${module}"
  else
    exit 1
  fi
done

