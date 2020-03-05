#!/bin/sh
set -ex

echo "-----------------------------------------------------------------"
echo "Sanitizing Image"
echo "-----------------------------------------------------------------"

USERNAME=root

# Remove interactive login shell for everybody but user.
sed -i -r '/^'${USERNAME}}':/! s#^(.*):[^:]*$#\1:/sbin/nologin#' /etc/passwd

sysdirs="
  /usr/bin
"

# Clean up apk
apk cache clean || true
find $sysdirs -xdev -regex '.*apk.*' -exec rm -fr {} +

rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Remove apt-get tool and admin commands and programs that could be dangerous.
find $sysdirs -xdev \( \
  -name apk -o \
  -name curl -o \
  -name wget -o \
  -name ln -o \
  -name chown -o \
  -name sudo -o \
  -name chmod \
  \) -delete

# Remove root homedir since we do not need it.
rm -fr /root

# Update notification that system has been scrubbed
cat /dev/null > /etc/future

/bin/cat << EOF  >> /etc/future
***********************************************************************
**** This image has successfully removed apk and other admin level ****
**** commands that were required for installing packages and       ****
**** configuring the application.                                  ****
***********************************************************************
EOF

# Print Completion of Hardening
cat /etc/future

# Self Destruct
rm $0
