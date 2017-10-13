#!/bin/bash
function movedeb {
  mv $1 ./dist/`dpkg -f $1 package`_`dpkg -f $1 version`.deb
}


VERSION=`git describe --tags --first-parent`

LICENSE_PATH="LICENSE.txt"
LICENSE_TMP_PATH="/tmp/LICENSE.txt"
sed 's/VERSION/'"$VERSION"'/' <$LICENSE_PATH >$LICENSE_TMP_PATH
cp $LICENSE_TMP_PATH $LICENSE_PATH

CONTENTS_PATH="svarog/src/main/resources/org/signalml/help/contents.html"
CONTENTS_TMP_PATH="/tmp/contets.html"
sed 's/VERSION/'"$VERSION"'/' <$CONTENTS_PATH >$CONTENTS_TMP_PATH
cp CONTENTS_TMP_PATH CONTENTS_PATH

set -e
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$(VERSION)

sed -i 's/VRS/'"$VERSION"'/g' svarog-standalone/src/deb/control/control

# Skip tests without even compiling them. Whe are compiling them and running in previous job, so it isn't necessary.
# To compile tests run `mvn test-compile compile`
mvn clean package -DskipTests
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

equivs-build svarog-all