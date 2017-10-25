#!/bin/bash
source ./functions.sh
set -e

function movedeb {
  mv $1 ./dist/`dpkg -f $1 package`_`dpkg -f $1 version`.deb
}

build

mkdir -p dist
movedeb svarog-standalone/target/*.deb
movedeb plugins/Artifact/target/*.deb
movedeb plugins/BookReporter/target/*.deb
movedeb plugins/FFTSignalTool/target/*.deb
movedeb plugins/PluginToolCommon/target/*.deb
movedeb plugins/SignalAnalysisPlugin/target/*.deb
movedeb plugins/Stager/target/*.deb
cp svarog-all.template dist/svarog-all

cd dist
equivs-build svarog-all