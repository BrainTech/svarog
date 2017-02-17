#!/bin/sh
# Before you run this script, follow the instructions in README.md.
set -o errexit
cd `dirname $0`
genisoimage -o dvd.iso -f -J -R dvd
