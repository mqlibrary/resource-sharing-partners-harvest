#!/bin/bash

APP=resource-sharing-partners-sync-0.1.6.jar

export ALMA_URL=https://api-ap.hosted.exlibrisgroup.com/almaws/v1
export ELASTIC_URL=http://localhost:9200/
export ELASTIC_USR=none
export ELASTIC_PWD=none
export SYNC_HOST=0.0.0.0
export SYNC_PORT=8080
export SYNC_PATH=partner-sync

JAVA_OPTS="-Xms256m -Xmx256m"

java $JAVA_OPTS -jar $APP $*
