#!/usr/bin/env bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
source "$SCRIPTPATH/common/functions.sh"
set -e

build

dest="dist/svarog-$VERSION-standalone"
mkdir -p "$dest"

mv standalone-package-files/* "$dest"
mv svarog-standalone/target/*.jar "$dest/svarog-standalone.jar"
cp LICENSE.txt "$dest"

cd dist
zip -r svarog-$VERSION-standalone.zip svarog-$VERSION-standalone
