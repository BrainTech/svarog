#!/usr/bin/env bash
cd dist
mkdir jre_temp_lin
tar xvzf ../installer_builder/build/jre/zulu11.37.17-ca-jre11.0.6-linux_x64.tar.gz -C jre_temp_lin
mv jre_temp_lin/zulu* jre_lin


mkdir svarog_linux
cp -r ../svarog-standalone-package svarog_linux/svarog
cp -r jre_lin svarog_linux/jre
cp ../installer_builder/build/mac/svarog.sh svarog_linux/svarog.sh
chmod +x svarog_linux/svarog.sh
