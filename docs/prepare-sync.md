# Cloning, Building, Configuring and Running the Sync Server

The Sync Server is on GitHub at:
[https://github.com/mqlibrary/resource-sharing-partners-sync/](https://github.com/mqlibrary/resource-sharing-partners-sync/). This document is using version __0.1.3__ of the sync server. You should always use the latest version where possible.

1. Clone the repository:

    ```bash
    git clone https://github.com/mqlibrary/resource-sharing-partners-sync/
    ```

1. Build the project:

    ```bash
    cd resource-sharing-partners-sync
    mvn -DskipTests -P prd clean package
    ```

1. Extract the project:

    ```bash
    cd ..
    tar xzf resource-sharing-partners-sync/target/resource-sharing-partners-sync-0.1.3-dist.tar.gz
    ```

1. There should now be a new folder in the current folder: __resource-sharing-partners-sync-0.1.3__.
    Go into this folder and configure the __resource-sharing-partners-sync.sh__ file.
    
    ```bash
    cd resource-sharing-partners-sync
    ```

    The default __resource-sharing-partners-sync.sh__ file looks like this:

    ```bash
    #!/bin/bash

    APP=resource-sharing-partners-sync-0.1.3.jar

    export ALMA_URL=${alma.url}
    export ELASTIC_URL=${elastic.url}
    export ELASTIC_USR=${elastic.usr}
    export ELASTIC_PWD=${elastic.pwd}
    export SYNC_HOST=${sync.host}
    export SYNC_PORT=${sync.port}
    export SYNC_PATH=${sync.path}

    JAVA_OPTS="-Xms1g -Xmx1g"

    java $JAVA_OPTS -jar $APP $*
    ```

    Edit the file and replace the settings (simple defaults in example):

    ```bash
    #!/bin/bash

    APP=resource-sharing-partners-sync-0.1.3.jar

    export ALMA_URL=https://api-ap.hosted.exlibrisgroup.com/almaws/v1
    export ELASTIC_URL=http://localhost:9200/
    export ELASTIC_USR=none
    export ELASTIC_PWD=none
    export SYNC_HOST=0.0.0.0
    export SYNC_PORT=8080
    export SYNC_PATH=partner-sync

    JAVA_OPTS="-Xms256m -Xmx256m"

    java $JAVA_OPTS -jar $APP $*
    ```

1. To run the server, execute the __resource-sharing-partners-sync.sh__ script (ensure it is executable).

    ```bash
    chmod 755 resource-sharing-partners-sync.sh
    ./resource-sharing-partners-sync.sh
    ```

