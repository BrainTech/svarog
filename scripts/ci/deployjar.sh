#!/bin/bash
set -e
#needs variables:
#ADDRESS
#USR
#PSW
#SVAROG_LOCATION

cd dist
SVAROG_PKG=`ls svarog-*-standalone.zip`


echo "RewriteEngine On
RewriteRule ^svarog-latest\.zip $SVAROG_LOCATION/$SVAROG_PKG [L,R=302]" > .htaccess

sshpass -p $PSW sftp -o StrictHostKeyChecking=no $USR@$ADDRESS << EOT
mput svarog-*-standalone.zip
put .htaccess
EOT