#!/bin/bash
#
# Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Amazon Software License (the "License").
# You may not use this file except in compliance with the License.
# A copy of the License is located at
#
# http://aws.amazon.com/asl/
#
# or in the "license" file accompanying this file. This file is distributed
# on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
# express or implied. See the License for the specific language governing
# permissions and limitations under the License.

# AWS EMR bootstrap script
# for installing Dr. Elephant on AWS EMR 5+
#
# 2019-05-05 - Tom Zeng tomzeng@amazon.com, initial version
# 2019-05-09 - Tom Zeng tomzeng@amazon.com, add SparkLens

#
# Usage:
# --port <port> - dr elephant port, default to 8087, optional
# --db-url <db url> - db url (must be MySQL 5.7+ or compatible ), if not specified, mysql 5.7 will be installed via docker, optional
# --db-name <db name> - database name, default to drelephant, optional
# --db-user <db user> - db user name (with create database privilege), optional
# --db-password <db password> - db password, optional
# --hadoop-ver <version> - specify the Hadoop version, default to what's on the cluster, optional (currently 2.7.3, 2.8.3, and 2.8.5 are supported)
# --sparklens - set up SparkLens for profiling Spark jobs (results saved to /user/hadoop/sparklens.json/ in HDFS)
# --sparklens-ver <version> - specify the Sparklens version, default to 0.3.0, optional
# --sparklens-datadir <hdfs directory or s3 folder> - specify the Sparklens report data directory default to /user/hadoop/sparklens.json in HDFS, optional


set -x -e

# check for master node
IS_MASTER=false
if grep isMaster /mnt/var/lib/info/instance.json | grep true;
then
  IS_MASTER=true
fi

# error message
error_msg ()
{
  echo 1>&2 "Error: $1"
}

DR_ELEPHANT_PORT=8087
DB_URL=
DB_NAME=drelephant
DB_USER=root
DB_PASSWORD=drelephant
DR_MEM=1024
HADOOP_VER=$(ruby -e "puts '`hadoop version | grep Hadoop`'.split('-')[0].split()[1]")
SPARKLENS=false
SPARKLENS_VER=0.3.0
SPARKLENS_DATADIR=sparklens.json # /user/hadoop/sparklens.json

while [ $# -gt 0 ]; do
  case "$1" in
    --port)
      shift
      DR_ELEPHANT_PORT=$1
      ;;
    --db-url)
      shift
      DB_URL=$1
      ;;
    --db-name)
      shift
      DB_NAME=$1
      ;;
    --db-user)
      shift
      DB_USER=$1
      ;;
    --db-password)
      shift
      DB_PASSWORD=$1
      ;;
    --memory)
      shift
      DR_MEM=$1
      ;;
    --hadoop-ver)
      shift
      HADOOP_VER=$1
      ;;
    --sparklens-ver)
      shift
      SPARKLENS_VER=$1
      SPARKLENS=true
      ;;
    --sparklens-datadir)
      shift
      SPARKLENS_DATADIR=$1
      SPARKLENS=true
      ;;
    --sparklens)
      SPARKLENS=true
      ;;
    -*)
      # do not exit out, just note failure
      error_msg "unrecognized option: $1"
      ;;
    *)
      break;
      ;;
  esac
  shift
done

if [ $SPARKLENS == true ]; then
  while [ ! -f /etc/spark/conf/spark-defaults.conf ]
  do
    sleep 5
  done
  sleep 10
  sudo bash -c "echo 'spark.jars.packages qubole:sparklens:${SPARKLENS_VER}-s_2.11' >> /etc/spark/conf/spark-defaults.conf"
  sudo bash -c "echo 'spark.extraListeners com.qubole.sparklens.QuboleJobListener' >> /etc/spark/conf/spark-defaults.conf"
  sudo bash -c "echo 'spark.sparklens.reporting.disabled false' >> /etc/spark/conf/spark-defaults.conf"
  sudo bash -c "echo 'spark.sparklens.data.dir $SPARKLENS_DATADIR' >> /etc/spark/conf/spark-defaults.conf"
fi

if [ "$DB_URL" == "" ]; then
  sudo yum install -y docker
  sudo service docker start

  sudo docker pull mysql/mysql-server:5.7

  export MYSQL_DATA_DIR=/mnt/mysql-drelephant
  mkdir -p $MYSQL_DATA_DIR
  export MYSQL_ROOT_PASSWORD=$DB_PASSWORD
  sudo docker run --name mysql-drelephant -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD -e MYSQL_ROOT_HOST=% -v $MYSQL_DATA_DIR:/var/lib/mysql -d mysql/mysql-server:5.7

  DB_URL=`sudo docker inspect mysql-drelephant | jq '.[0].NetworkSettings.IPAddress' | sed -e 's/^"//' -e 's/"$//'`

  while [ ! -f /mnt/mysql-drelephant/ibdata1 ]
  do
    sleep 5
  done
  sleep 10
fi

mysql -h $DB_URL -P 3306 -uroot -p$DB_PASSWORD -e "drop database if exists $DB_NAME;"
mysql -h $DB_URL -P 3306 -uroot -p$DB_PASSWORD -e "create database $DB_NAME;"

export HADOOP_HOME=/usr/lib/hadoop

export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop

export SPARK_HOME=/usr/lib/spark

export SPARK_CONF_DIR=/usr/lib/spark/conf

cd /mnt
# the following assumes that dr-elephant-2.1.7-hadoop$HADOOP_VER.zip exists
aws s3 cp s3://tomzeng-perf2/dr-elephant-dist/dr-elephant-2.1.7-hadoop$HADOOP_VER.zip .
unzip dr-elephant-2.1.7-hadoop$HADOOP_VER.zip
cd dr-elephant-2.1.7
chmod +x bin/*

sudo sed -i "s/port=8080/port=$DR_ELEPHANT_PORT/g" app-conf/elephant.conf # 8080 is the default
sudo sed -i "s/port=8087/port=$DR_ELEPHANT_PORT/g" app-conf/elephant.conf # in case 8087 is used in builds as new default
sudo sed -i "s/db_url=localhost/db_url=$DB_URL/g" app-conf/elephant.conf
sudo sed -i "s/db_user=root/db_user=$DB_USER/g" app-conf/elephant.conf
sudo sed -i "s/db_password=\"\"/db_password=$DB_PASSWORD/g" app-conf/elephant.conf
sudo sed -i "s/\-mem\ 1024/\-mem\ $DR_MEM/g" app-conf/elephant.conf # 1024 is the default
sudo sed -i "s/\-mem\ 2048/\-mem\ $DR_MEM/g" app-conf/elephant.conf # in case 2048 is used in build as new default

bin/start.sh `pwd`/app-conf