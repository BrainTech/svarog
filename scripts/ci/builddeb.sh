#!/usr/bin/env bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
source "$SCRIPTPATH/common/functions.sh"
set -e

function movedeb {
  mv $1 ./dist/`dpkg -f $1 package`_`dpkg -f $1 version`.deb
}

build

mkdir -p dist
movedeb svarog-standalone/target/*.deb
