#!/bin/sh
if [ `id -u` -ne 0 ] ; then
  echo "This script should be run by root, e.g."
  echo "  sudo $0"
  exit 1
fi

# Installation of Java and development tools
apt-get install -y maven netbeans protobuf-compiler openjdk-8-jdk openjfx
