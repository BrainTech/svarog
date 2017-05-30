#!/bin/bash
set -e
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$(git describe --tags --first-parent)
mvn clean package

VERSION=$(git describe --tags --first-parent)

mkdir -p dist/svarog-$VERSION-standalone/plugins


mv standalone-package-files/* dist/svarog-$VERSION-standalone/


mv svarog-standalone/target/*.jar dist/svarog-$VERSION-standalone/svarog-standalone.jar

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
zip -r svarog-$VERSION-standalone.zip svarog-$VERSION-standalone
