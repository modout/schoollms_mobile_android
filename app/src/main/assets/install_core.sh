#!/bin/bash

#Check system information - cloud 
echo "Checking system information..."
cd /sdcard/GNURoot/home
tar -xvf install.tar.gz
echo "running the new script!"
./install_schoollms_core.sh $1 $2 $3
#go to real
