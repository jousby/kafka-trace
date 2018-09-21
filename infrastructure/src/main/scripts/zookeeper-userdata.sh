#!/usr/bin/env bash

# Install Java
sudo yum update -y
sudo yum install -y git java-1.8.0-openjdk-devel

# Install confluent
mkdir opt
cd opt
wget https://packages.confluent.io/archive/3.3/confluent-oss-3.3.2-2.11.tar.gz
tar -xvf confluent-oss-3.3.2-2.11.tar.gz

# Configure zookeeper
    # prep zookeeper.props
    # create $data_dir/myid file
    # boot loader
