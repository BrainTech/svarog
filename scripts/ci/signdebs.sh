#!/usr/bin/env bash
set -e
#GPG_KEY - enviroment variable - private key in base64
echo "$GPG_KEY" | base64 -d | gpg --allow-secret-key-import --import -
debsigs --sign=origin svarog*.deb
