#!/bin/sh
# Run Svarog in background, redirecting output to log file.
JAVA="/usr/java/latest/bin/java"
OUTPUT="svarog.log"
SVAROG="svarog-standalone.jar"
if [ ! -x "$JAVA" ] ; then
  JAVA="java"
fi
cd `dirname $0`
if ! touch $OUTPUT >/dev/null 2>&1 ; then
  OUTPUT="$HOME/$OUTPUT"
fi
nohup $JAVA -jar svarog-standalone.jar >>"$OUTPUT" 2>&1 &
