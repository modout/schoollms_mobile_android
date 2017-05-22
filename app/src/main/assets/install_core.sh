#!/bin/bash

#Check system information - cloud 
echo "Checking system information..."
cd /sdcard/GNURoot/home
tar -xvf install.tar.gz
echo '-----------PWD-------------'
pwd
echo '-----------PWD END-------------'
echo "running the new script!"
./install_schoollms_core.sh
#go to real
