#!/bin/bash

CURRENT_TIME=`date +'%Y%m%d%H%M%S'`

ES_CERT=/path/to/cert
ES_USER=username
ES_PASS=password
ES_BASE=http://elastic.svc.library.local/
ES_REPO=mqlibraryes-snapshot-repo
ES_SNAP=snap-$CURRENT_TIME

ES_DATA="{ \"indices\": \"partner-*\", \"ignore_unavailable\": true, \"include_global_state\": false }"

URL="$ES_BASE/_snapshot/$ES_REPO/$ES_SNAP" 

echo $ES_DATA | http --auth "$ES_USER:$ES_PASS" --verify=$ES_CERT PUT $URL
