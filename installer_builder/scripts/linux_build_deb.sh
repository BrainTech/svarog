#!/usr/bin/env bash
VERSION=`git describe --tags`
mkdir svarog_linux_deb
cp -r installer_builder/build/linux_deb/* svarog_linux_deb/
cp -r svarog_linux svarog_linux_deb/usr/share/
sed -i "s/__VERSION__/$VERSION/g" svarog_linux_deb/DEBIAN/control
dpkg-deb --build svarog_linux_deb
mv svarog_linux_deb.deb svarog.deb
