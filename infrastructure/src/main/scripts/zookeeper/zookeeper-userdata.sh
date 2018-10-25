#!/usr/bin/env bash

# Install Java
yum update -y
yum install -y git java-1.8.0-openjdk-devel

# Install confluent platform
CP_HOME=/opt/confluent
mkdir -p $CP_HOME
wget https://packages.confluent.io/archive/3.3/confluent-oss-3.3.2-2.11.tar.gz -O /tmp/confluent.tgz
tar -xvf /tmp/confluent.tgz -C $CP_HOME --strip-components 1

# Drop in our replacement zookeeper properties file
mv $CP_HOME/etc/kafka/zookeeper.properties $CP_HOME/etc/kafka/zookeeper.properties.orig
mv /tmp/zookeeper.properties $CP_HOME/etc/kafka/zookeeper.properties

# Create the data dir and id file
ZK_DATA_DIR=/tmp/zookeeper
mkdir -p $ZK_DATA_DIR
cat > $ZK_DATA_DIR/myid << EOF
1
EOF

# Fix file permissions so things can run as ec2-user
chown -R ec2-user:ec2-user $CP_HOME
chown -R ec2-user:ec2-user $ZK_DATA_DIR

# Enable zookeeper to run via systemd (see /etc/systemd/system/zookeeper.service)
systemctl daemon-reload
systemctl enable zookeeper.service
