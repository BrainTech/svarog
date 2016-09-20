#!/bin/bash
set -e
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$(git describe --tags --first-parent)
mvn clean package

VERSION=$(git describe --tags --first-parent)

mkdir -p dist/svarog-$VERSION-standalone/plugins

mv svarog-standalone/target/*.jar dist/svarog-$VERSION-standalone/

mv plugins/Artifact/target/*.jar dist/svarog-$VERSION-standalone/plugins/
mv plugins/Artifact/target/*.xml dist/svarog-$VERSION-standalone/plugins/

mv plugins/BookReporter/target/*.jar dist/svarog-$VERSION-standalone/plugins/
mv plugins/BookReporter/target/*.xml dist/svarog-$VERSION-standalone/plugins/

mv plugins/FFTSignalTool/target/*.jar dist/svarog-$VERSION-standalone/plugins/
mv plugins/FFTSignalTool/target/*.xml dist/svarog-$VERSION-standalone/plugins/

mv plugins/PluginToolCommon/target/*.jar dist/svarog-$VERSION-standalone/plugins/
mv plugins/PluginToolCommon/target/*.xml dist/svarog-$VERSION-standalone/plugins/

mv plugins/SignalAnalysisPlugin/target/*.jar dist/svarog-$VERSION-standalone/plugins/
mv plugins/SignalAnalysisPlugin/target/*.xml dist/svarog-$VERSION-standalone/plugins/

mv plugins/Stager/target/*.jar dist/svarog-$VERSION-standalone/plugins/
mv plugins/Stager/target/*.xml dist/svarog-$VERSION-standalone/plugins/

cd dist
tar -zcvf svarog-$VERSION-standalone.tar.gz svarog-$VERSION-standalone
