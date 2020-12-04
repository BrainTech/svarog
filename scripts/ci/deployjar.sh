#!/bin/bash
set -e
#needs variables:
#ADDRESS
#USR
#PSW
#SVAROG_LOCATION

SVAROG_PKG_WIN=`ls Svarog_installer*.exe`
SVAROG_PKG_MAC=`ls svarog_mac*.zip` 
SVAROG_PKG_LIN=`ls svarog*.deb`
SVAROG_PKG_STANDALONE=`ls svarog-*-standalone.zip`


echo "RewriteEngine On
RewriteRule ^svarog-latest-win\.zip $SVAROG_LOCATION/svarog/$SVAROG_PKG_WIN [L,R=302]
RewriteRule ^svarog-latest-lin\.zip $SVAROG_LOCATION/svarog/$SVAROG_PKG_LIN [L,R=302]
RewriteRule ^svarog-latest-mac\.zip $SVAROG_LOCATION/svarog/$SVAROG_PKG_MAC [L,R=302]
RewriteRule ^svarog-latest-standalone\.zip $SVAROG_LOCATION/svarog/$SVAROG_PKG_STANDALONE [L,R=302]" > .htaccess

sshpass -p $PSW sftp -o StrictHostKeyChecking=no $USR@$ADDRESS << EOT
cd svarog
mput Svarog_installer*.exe
mput svarog_mac*.zip
mput svarog*.deb
mput svarog-*-standalone.zip
put .htaccess
EOT
