#!/bin/bash
set -e
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$(git describe --tags)
mvn clean package
mkdir dist
mv svarog-standalone/target/*.deb ./dist
mv plugins/Artifact/target/*.deb ./dist
mv plugins/BookReporter/target/*.deb ./dist
mv plugins/FFTSignalTool/target/*.deb ./dist
mv plugins/PluginToolCommon/target/*.deb ./dist
#mv plugins/SFTestPlugin/target/*.deb ./dist
mv plugins/SignalAnalysisPlugin/target/*.deb ./dist
mv plugins/Stager/target/*.deb ./dist
sed "s/GIT_VERSION/$(git describe --tags)/g" svarog-all.template > dist/svarog-all
cd dist
equivs-build svarog-all