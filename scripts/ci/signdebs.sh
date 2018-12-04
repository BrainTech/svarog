#!/usr/bin/env bash
set -e
#GPG_KEY - enviroment variable - private key in base64
echo "$GPG_KEY" | base64 -d | gpg --allow-secret-key-import --import -
cd dist
debsigs --sign=origin svarog-bookreporter_*.deb
debsigs --sign=origin svarog-fftsignaltool_*.deb
debsigs --sign=origin svarog-plugintoolcommon_*.deb
debsigs --sign=origin svarog-signalanalysisplugin_*.deb
debsigs --sign=origin svarog_*.deb
debsigs --sign=origin svarog-all_*.deb
