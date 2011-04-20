#!/bin/bash

set -ex
rm -rf ~/.m2/repository/org/signalml/svarog
mvn -N install
for p in src/plugins/{FFT,FFTSignalTool,PluginToolCommon,Artifact} src/izpack; do
    (
	cd $p;
	mvn -N install
    )
done
