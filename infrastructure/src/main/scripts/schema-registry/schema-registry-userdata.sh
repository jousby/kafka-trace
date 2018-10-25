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
mv $CP_HOME/etc/schema-registry/schema-registry.properties $CP_HOME/etc/schema-registry/schema-registry.properties.orig
mv /tmp/schema-registry.properties $CP_HOME/etc/schema-registry/schema-registry.properties

# Fix file permissions so things can run as ec2-user
chown -R ec2-user:ec2-user $CP_HOME

# Enable Confluent Schema Registry to run via systemd (see /etc/systemd/system/schema-registry.service)
systemctl daemon-reload
systemctl enable schema-registry.service
