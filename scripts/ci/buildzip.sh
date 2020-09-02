#!/usr/bin/env bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
source "$SCRIPTPATH/common/functions.sh"

sed -i "s#__SENTRY_DSN_TEMPLATE__#$OBCI_SENTRY_TEMPLATE_DSN#g" "svarog/src/main/resources/org/signalml/app/config/signalml_defaults.properties"

set -e

build

dest="dist/svarog-$VERSION-standalone"
mkdir -p "$dest"

mv standalone-package-files/* "$dest"
mv svarog-standalone/target/*.jar "$dest/svarog-standalone.jar"
cp LICENSE.txt "$dest"

cd dist
zip -r svarog-$VERSION-standalone.zip svarog-$VERSION-standalone
