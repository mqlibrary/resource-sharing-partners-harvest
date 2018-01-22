#!/bin/bash

CURRENT_TIME=`date +'%Y%m%d%H%M%S'`

echo "{ \"indices\": \"partner-*\", \"ignore_unavailable\": true, \"include_global_state\": false }" |  http --pretty format --auth "elastic:elastic2017" --verify=/etc/pki/tls/certs/mq-lib-ca.crt PUT "https://elastic.svc.library.local/_snapshot/mqlibraryes-snapshot-repo/snap-$CURRENT_TIME"
