@echo off

set APP=${project.artifactId}-${project.version}.jar

set JAVA_OPTS=-Xms256m -Xmx256m
  
java %JAVA_OPTS% -jar %APP% %*
