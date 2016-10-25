#!/bin/sh
# Before you run this script:
#
# 1. Create directory dvd/apps and download the VLC installer from
#      http://www.videolan.org/vlc/download-windows.html
#    into that directory.
#
# 2. Create directory dvd/data and copy all data to be distributed
#    into that directory.
#
# 3. Compile Svarog as usual (mvn package) and copy
#    the generated svarog-standalone JAR file into directory dvd.
#
set -o errexit
cd `dirname $0`
genisoimage -o dvd.iso -f -J -R dvd
echo 'Now you can burn the DVD using the generated ISO image (dvd.iso).'
