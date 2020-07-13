#!/bin/bash
cd dist
mkdir jre_temp_win
unzip ../installer_builder/build/jre/zulu11.37.17-ca-jre11.0.6-win_x64.zip -d jre_temp_win
mv jre_temp_win/zulu* jre_win

mkdir svarog_win
cp -r ../svarog-standalone-package svarog_win/svarog
cp -r jre_win svarog_win/jre
cp ../installer_builder/build/svarog_exe/svarog.exe svarog_win/svarog.exe
