@echo off

set APP=${project.artifactId}-${project.version}.jar

set JAVA_OPTS=-Xms256m -Xmx256m

REM configure for proxy use
REM PROXY_HOST=
REM PROXY_PORT=
REM JAVA_OPTS=%JAVA_OPTS% -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT -Dhttps.proxyHost=$PROXY_HOST -Dhttps.proxyPort=$PROXY_PORT

  
java %JAVA_OPTS% -jar %APP% %*
