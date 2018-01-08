#!/bin/bash

APP=${project.artifactId}-${project.version}.jar

JAVA_OPTS="-Xms256m -Xmx256m"
  
java $JAVA_OPTS -jar $APP $*
