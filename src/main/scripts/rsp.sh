#!/bin/bash

APP=${project.artifactId}-${project.version}.jar

JAVA_OPTS="-Xms256m -Xmx256m"

# configure for proxy use
#PROXY_HOST=
#PROXY_PORT=
#JAVA_OPTS="$JAVA_OPTS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT -Dhttps.proxyHost=$PROXY_HOST -Dhttps.proxyPort=$PROXY_PORT"


java $JAVA_OPTS -jar $APP $*
