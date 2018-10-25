#!/usr/bin/env bash

# Install Java
yum update -y
yum install -y java-1.8.0-openjdk-devel

# Install confluent platform
CP_HOME=/opt/confluent
mkdir -p $CP_HOME
wget https://packages.confluent.io/archive/3.3/confluent-oss-3.3.2-2.11.tar.gz -O /tmp/confluent.tgz
tar -xvf /tmp/confluent.tgz -C $CP_HOME --strip-components 1

# Drop in our replacement Kafka server properties file
mv $CP_HOME/etc/kafka/server.properties $CP_HOME/etc/kafka/server.properties.orig
mv /tmp/kafka.properties $CP_HOME/etc/kafka/server.properties

# Fix file permissions so things can run as ec2-user
chown -R ec2-user:ec2-user $CP_HOME

# Update max number of file descriptors, Confluent recommends a number > 100,000
cat > /etc/security/limits.conf << EOF
*         hard    nofile      500000
*         soft    nofile      500000
root      hard    nofile      500000
root      soft    nofile      500000
EOF

sysctl -p

# Enable kafka to run via systemd (see /etc/systemd/system/kafka.service)
systemctl daemon-reload
systemctl enable kafka.service
