#!/bin/bash
set -e
#needs variables:
#DEB_SRV
#DEB_USER
#SSHPASSV
#RELEASE


DEBUID=$(uuidgen)
sshpass -p $SSHPASS ssh -o StrictHostKeyChecking=no $DEB_USER@$DEB_SRV "mkdir -p -v ~/incoming/$RELEASE/$DEBUID/"
sshpass -p $SSHPASS scp -o StrictHostKeyChecking=no -v ./dist/* $DEB_USER@$DEB_SRV:~/incoming/$RELEASE/$DEBUID/

sshpass -p $SSHPASS ssh -o StrictHostKeyChecking=no $DEB_USER@$DEB_SRV "~/trigger_rebuild.sh $DEBUID $RELEASE"
