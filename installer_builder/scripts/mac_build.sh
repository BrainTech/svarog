#!/bin/bash
cd dist
unzip ../installer_builder/build/jre/zulu11.37.17-ca-jre11.0.6-macosx_x64.zip -d jre_temp
mv jre_temp/zulu* jre_mac


mkdir svarog_mac.app
mkdir svarog_mac.app/Contents
mkdir svarog_mac.app/Contents/MacOS
mkdir svarog_mac.app/Contents/Resources

cp ../installer_builder/build/mac/Info.plist svarog_mac.app/Contents
cp ../installer_builder/build/mac/braintech.icns svarog_mac.app/Contents/Resources/braintech.icns
cp ../installer_builder/build/mac/svarog.sh svarog_mac.app/Contents/MacOS/svarog.sh
chmod +x svarog_mac.app/Contents/MacOS/svarog.sh
cp -r jre_mac svarog_mac.app/Contents/MacOS/jre
cp -r ../svarog-standalone-package svarog_mac.app/Contents/MacOS/svarog
