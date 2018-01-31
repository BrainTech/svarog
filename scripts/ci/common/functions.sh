#!/usr/bin/env bash
VERSION=`git describe --tags --first-parent`

#usage:
#update_version_in_file 'file_path'
#VERSION=$VERSION
_update_version_in_file () {
  path=$1
  tmp="/tmp/${path##*/}"
  sed 's/VERSION/'"$VERSION"'/' <$path >$tmp
  cp $tmp $path
}

update_version_in_files () {
  _update_version_in_file "LICENSE.txt"
  _update_version_in_file "svarog/src/main/resources/org/signalml/help/contents.html"
  _update_version_in_file "svarog-standalone/src/deb/control/control"
  _update_version_in_file "svarog-all.template"
}

build () {
  update_version_in_files
  mvn versions:set -DgenerateBackupPoms=false -DnewVersion="$VERSION" -X
  # Skip tests without even compiling them. Whe are compiling them and running in previous job,
  # so it isn't necessary. To compile tests run `mvn test-compile compile`
  mvn clean package -DskipTests
}
