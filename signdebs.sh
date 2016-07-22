#!/usr/bin/env bash
#GPG_KEY - enviroment variable - private key in base64
echo "$GPG_KEY" | base64 -d | gpg --allow-secret-key-import --import -
cd dist
debsigs --sign=origin Artifact_*.deb
debsigs --sign=origin BookReporter_*.deb
debsigs --sign=origin FFTSignalTool_*.deb
debsigs --sign=origin PluginToolCommon_*.deb
debsigs --sign=origin SignalAnalysisPlugin_*.deb
debsigs --sign=origin Stager_*.deb
debsigs --sign=origin svarog-standalone_*.deb