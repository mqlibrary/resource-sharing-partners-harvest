#!/bin/bash

CURRENT_TIME=`date +'%Y%m%d%H%M%S'`

ES_USER=username
ES_PASS=password
ES_BASE=http://elastic.svc.lib.mq.edu.au
ES_REPO=backups
ES_SNAP=snap-$CURRENT_TIME

ES_DATA="{ \"indices\": \"partner-*\", \"ignore_unavailable\": true, \"include_global_state\": false }"

URL="$ES_BASE/_snapshot/$ES_REPO/$ES_SNAP" 

curl -s --user $ES_USER:$ES_PASS -XPUT "$URL" -H "Content-Type: application/json" -H "Accept: application/json" -d $ES_DATA
