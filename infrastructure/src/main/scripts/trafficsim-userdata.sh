#!/usr/bin/env bash

sudo yum update -y
sudo yum install -y git java-1.8.0-openjdk-devel

# install skdman
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# install sbt
sdk install sbt

