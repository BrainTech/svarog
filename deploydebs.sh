#!/bin/bash
#needs variables:
#MASTER
#DEB_SRV
#DEB_USER
#SSHPASSV
#RELEASE


DEBUID=$(uuidgen)
sshpass -p $SSHPASS ssh -o StrictHostKeyChecking=no $DEB_USER@$DEB_SRV "mkdir -p -v ~/incoming/$RELEASE/$DEBUID/"
sshpass -p $SSHPASS scp -o StrictHostKeyChecking=no -v ./dist/* $DEB_USER@$DEB_SRV:~/incoming/$RELEASE/$DEBUID/

if [ $MASTER = "1" ]; then
    echo "Login to master repo SSH and manually run: 
~/trigger_rebuild.sh $DEBUID $RELEASE


"
else
    sshpass -p $SSHPASS ssh -o StrictHostKeyChecking=no $DEB_USER@$DEB_SRV "~/trigger_rebuild.sh $DEBUID $RELEASE"
fi