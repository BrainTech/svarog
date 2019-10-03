#!/usr/bin/env bash
VERSION=`git describe --tags`

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
  for L in svarog/src/main/resources/org/signalml/help/* ; do
    _update_version_in_file "$L/contents.html"
  done
  _update_version_in_file "svarog-standalone/src/deb/control/control"
}

build () {
  update_version_in_files
  mvn versions:set -DgenerateBackupPoms=false -DnewVersion="$VERSION" -X
  # Skip tests without even compiling them. Whe are compiling them and running in previous job,
  # so it isn't necessary. To compile tests run `mvn test-compile compile`
  mvn clean package -DskipTests
}
