#!/bin/sh
if [ `id -u` -ne 0 ] ; then
  echo "This script should be run by root, e.g."
  echo "  sudo $0"
  exit 1
fi

# Oracle Java 8 repository
add-apt-repository -y ppa:webupd8team/java
apt-get update

# Automatically accepting Oracle licence
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# Installation of Java and development tools
apt-get install -y maven netbeans protobuf-compiler oracle-java8-installer
