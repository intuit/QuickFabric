#!/bin/bash

# https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/device_naming.html

data_mount=`df -h | grep -o /data`

mkdir /data

while [ -z $data_mount ]; do
 unformated_disk=`parted -l 2>&1 | grep unrecognised | grep -o "/dev/[a-z0-9]*"`

 if [ -z $unformated_disk ]; then
	echo "All disks formatted"
  for disk in `parted -l | grep -o "/dev/[a-z0-9]*"`; do
		mount_status=`df -h | grep -o $disk`
		echo $mount_status
		if [ -z $mount_status ]; then 
			echo "Mounting $mount_status"
			`mount $disk /data`
		fi
	done
		
 else
	echo "Found Unformated Disk $unformated_disk"
	echo "Formating disk"
	mkfs -t ext4 $unformated_disk
	mount $unformated_disk /data
	echo "Mounted formatted disk as /data/"
	echo $unformated_disk  /data ext4 defaults,nofail 0 2 >> /etc/fstab
 fi
 data_mount=`df -h | grep -o /data`
done


# Explicitly creating a change in the userdata file upon any change in the artifacts
# to initiate a new instance spin up.

touch /tmp/${md5_docker}
touch /tmp/${md5_frontend}
touch /tmp/${md5_middleware}
touch /tmp/${md5_db}

mkdir -p /data/quickfabric/docker
mkdir -p /data/quickfabric/mysql


YUM_CMD=$(which yum)
APT_GET_CMD=$(which apt-get)
if [[ ! -z $YUM_CMD ]]; then
  yum update -y && yum install -y docker curl unzip awscli maven  --enablerepo=*

  curl -sL https://rpm.nodesource.com/setup_13.x | bash -
  yum update -y && yum install -y nodejs  

  systemctl enable docker
  systemctl start docker

elif [[ ! -z $APT_GET_CMD ]]; then
  apt-get update -y && apt-get install -y docker.io curl unzip awscli maven npm
fi


# Installing applications

APP_HOME="/data/quickfabric/application"
DOWNLOAD_HOME="/data/quickfabric/downloads"
PERSISTANT_HOME="/data/quickfabric/persistant"

mkdir -p $APP_HOME 
mkdir -p $DOWNLOAD_HOME
mkdir -p $PERSISTANT_HOME


frontend_file_md5=`aws s3api get-object text-content --bucket ${bucket_name} --key ${frontend_artifacts_path} | python3 -c "import sys, json; print(json.load(sys.stdin)['ETag'])" | sed -e 's/"//g'`
frontend_local_file_md5=`md5sum /data/quickfabric/downloads/${frontend_artifacts_zip} | awk '{print $1}'`

echo $frontend_file_md5
echo $frontend_local_file_md5


middleware_file_md5=`aws s3api get-object text-content --bucket ${bucket_name} --key ${middleware_artifacts_path} | python3 -c "import sys, json; print(json.load(sys.stdin)['ETag'])" | sed -e 's/"//g'`
middleware_local_file_md5=`md5sum $DOWNLOAD_HOME/${middleware_artifacts_zip} | awk '{print $1}'`

echo $middleware_file_md5
echo $middleware_local_file_md5

if [[ $middleware_file_md5 == "$middleware_local_file_md5"  && -f $APP_HOME/Middleware/emr/target/QuickFabric_Services.jar && -f $APP_HOME/Middleware/schedulers/target/schedulers.jar ]] ; then
	echo "No changes dertected. Skipping Maven install"
else
        echo "Cleanup existing directories"
        rm -rf $APP_HOME/Middleware/*

	aws s3 cp  s3://${bucket_name}/${middleware_artifacts_path} $DOWNLOAD_HOME/
	unzip -o $DOWNLOAD_HOME/${middleware_artifacts_zip} -d $APP_HOME/Middleware/

	echo "Running Maven Install"
	cd  $APP_HOME/Middleware && mvn clean install -DskipTests -Dmaven.repo.local=$PERSISTANT_HOME/.m2
fi

if [[ $frontend_file_md5 == "$frontend_local_file_md5" &&  -d $APP_HOME/Frontend/build ]] ; then
	echo "No changes dertected. Skipping NPM install"
else
        echo "Cleanup existing directories"
        rm -rf $APP_HOME/Frontend/*

	aws s3 cp  s3://${bucket_name}/${frontend_artifacts_path} $DOWNLOAD_HOME/
	unzip -o $DOWNLOAD_HOME/${frontend_artifacts_zip} -d $APP_HOME/Frontend/
	cd $APP_HOME/Frontend && npm install && npm run build
fi

aws s3 cp  s3://${bucket_name}/${db_artifacts_path} $DOWNLOAD_HOME/
rm -rf $APP_HOME/DB/*
unzip -o $DOWNLOAD_HOME/${db_artifacts_zip} -d $APP_HOME/DB/

aws s3 cp  s3://${bucket_name}/${docker_artifacts_path} $DOWNLOAD_HOME/
unzip -o  $DOWNLOAD_HOME/${docker_artifacts_zip} -d $APP_HOME/

export MYSQL_PASSWORD="${MYSQL_PASSWORD}"
export AES_SECRET_KEY="${AES_SECRET_KEY}"

curl -L  --retry 5 \
     https://github.com/docker/compose/releases/download/${docker_compose_version}/docker-compose-`uname -s`-`uname -m` \
     -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

cd $APP_HOME/ && docker-compose up -d

docker exec  quickfabric_db bash -c "/patch/patch.sh"


