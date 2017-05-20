#!/bin/bash

#Check system information - cloud 
echo "Checking system information..."
tar -xvf install.tar.gz
cd install
echo "running the new script!"
./install_schoollms_core.sh
cat "It has been written" > hello.txt