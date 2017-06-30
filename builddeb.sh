#!/bin/bash
function movedeb {
  mv $1 ./dist/`dpkg -f $1 package`_`dpkg -f $1 version`.deb
}
set -e
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$(git describe --tags --first-parent)
mvn clean package
mkdir -p dist
movedeb svarog-standalone/target/*.deb
movedeb plugins/Artifact/target/*.deb
movedeb plugins/BookReporter/target/*.deb
movedeb plugins/FFTSignalTool/target/*.deb
movedeb plugins/PluginToolCommon/target/*.deb
movedeb plugins/SignalAnalysisPlugin/target/*.deb
movedeb plugins/Stager/target/*.deb
sed "s/GIT_VERSION/$(git describe --tags --first-parent)/g" svarog-all.template > dist/svarog-all
cd dist
mv svarog_*.deb svarog_`git describe --tags --first-parent`_all.deb
equivs-build svarog-all