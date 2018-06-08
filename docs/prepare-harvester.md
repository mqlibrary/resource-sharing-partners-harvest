# Cloning, Building, Configuring and Running the Harvester

The Harvester is on GitHub at:
[https://github.com/mqlibrary/resource-sharing-partners-harvest/](https://github.com/mqlibrary/resource-sharing-partners-harvest/).

1. Create an Outlook account if you do not already have one. It is also possible to use an Office365 institution account, but this will require some work to be done by your IT team that manages the Office365 organisation to configure an 'App'. The Outlook account is needed to create to receive Tepuna Status emails. You can simply go to https://outlook.live.com and create a new account.

    You will also need to configure the account to support OAuth2 login access. The process for this is outlined here: https://docs.microsoft.com/en-us/outlook/rest/get-started

    The URL for managing REST applications: https://apps.dev.microsoft.com/

1. Clone the repository:

    ```bash
    git clone https://github.com/mqlibrary/resource-sharing-partners-harvest/
    ```

1. Build the project:

    ```bash
    cd resource-sharing-partners-harvest
    mvn -DskipTests -P prd clean package
    ```

1. Extract the project:

    ```bash
    cd ..
    tar xzf resource-sharing-partners-harvest/target/resource-sharing-partners-harvest-0.0.18-dist.tar.gz
    ```

1. There should now be a new folder in the current folder: __resource-sharing-partners-harvest-0.0.18__. Go into this folder and configure the __app.properties__ file.
    ```bash
    cd resource-sharing-partners-harvest-0.0.18
    ```

    The default app.properties file should look like this:
    ```conf
    ws.url.elastic.index=
    ws.url.elastic.username=
    ws.url.elastic.password=

    ws.url.ilrs=http://www.nla.gov.au/apps/ilrs

    ws.url.ladd=https://www.nla.gov.au/librariesaustralia/connect/find-library/ladd-members-and-suspensions

    ws.url.tepuna=https://natlib.govt.nz/directory-of-new-zealand-libraries.csv

    outlook.url.endpoint=https://graph.microsoft.com/v1.0
    outlook.url.token=https://login.microsoftonline.com/common/oauth2/v2.0/token
    outlook.client.email=
    outlook.client.id=
    outlook.client.secret=
    ```

    You will need to configure the settings below:
    ```conf
    ws.url.elastic.index=http://localhost:9200/
    ws.url.elastic.username=none
    ws.url.elastic.password=none

    ws.url.ilrs=http://www.nla.gov.au/apps/ilrs

    ws.url.ladd=https://www.nla.gov.au/librariesaustralia/connect/find-library/ladd-members-and-suspensions

    ws.url.tepuna=https://natlib.govt.nz/directory-of-new-zealand-libraries.csv

    outlook.url.endpoint=https://graph.microsoft.com/v1.0
    outlook.url.token=https://login.microsoftonline.com/common/oauth2/v2.0/token
    outlook.client.email=your.email.address@outlook.com.au
    outlook.client.id=36205b60-a857-4eca-8aad-9c0094451fda
    outlook.client.secret=ajsd!*}RytTuT4Q{PO89TRY
    ```
    The outlook.client.* fields are based on your Outlook account - replace them with the appropriate values.

1. We need to generate an access/refresh token for accessing the Outlook instance and store it in the datastore under __/partner-configs/config/OUTLOOK__. It will look something like this:
    ```json
    {
        "token_type": "Bearer",
        "scope": "Mail.ReadWrite https://graph.microsoft.com/User.Read",
        "expires_in": 3600,
        "ext_expires_in": 0,
        "access_token": "EwBAA8l6BAAURSN...",
        "refresh_token": "MCUN82zuoFJNOm...",
        "id_token": "eyJ0eXAiOiJKV1QiLCJ..."
    } 
    ```

1. Save the __token__ in a file and then place it in the datastore under __partner-configs/config/OUTLOOK__. Assuming the token is saves as the file _refresh_token.json_:

    ```bash
    http PUT localhost:9200/partner-configs/config/OUTLOOK @/path/to/refresh_token.json
    ```

    Check the config:
    ```bash
    http localhost:9200/partner-configs/config/OUTLOOK/_source
    ```

    You should hopefully see something like:
    ```json
    HTTP/1.1 200 OK
    content-encoding: gzip
    content-length: 2157
    content-type: application/json; charset=UTF-8

    {
        "access_token": "EwBAA8l6BAAURSN...",
        "expires_in": "3600",
        "ext_expires_in": "0",
        "id_token": "eyJ0eXAiOiJKV1QiLCi...",
        "refresh_token": "MCXw1Zz87uCrq2...",
        "scope": "https://graph.microsoft.com/Mail.ReadWrite https://graph.microsoft.com/User.Read",
        "token_type": "Bearer"
    }
    ```

1. At this point we should be ready to perform a harvest:
    ```bash
    ./harvest.sh
    ```
    You should see some output similar to this:
    ```bash
    20180521T16:15:05.484 [INFO ]: executing: [Mon May 21 16:15:05 AEST 2018]
    20180521T16:15:06.300 [INFO ]: harvesting from: LADD
    20180521T16:15:06.591 [INFO ]: updating partners: 739
    20180521T16:15:06.602 [INFO ]: partners updated: 739
    20180521T16:15:06.602 [INFO ]: saving elasticsearch entities: 739
    20180521T16:15:06.602 [INFO ]: harvesting from: ILRS
    20180521T16:15:06.629 [INFO ]: skipping harvesting: ILRS
    20180521T16:15:06.629 [INFO ]: harvesting from: TEPUNA
    20180521T16:15:07.906 [INFO ]: updating partners: 419
    20180521T16:15:07.920 [INFO ]: partners updated: 419
    20180521T16:15:07.920 [INFO ]: saving elasticsearch entities: 419
    20180521T16:15:07.920 [INFO ]: harvesting from: OUTLOOK
    20180521T16:15:08.236 [INFO ]: updating partners: 23
    20180521T16:15:08.236 [INFO ]: partners updated: 23
    20180521T16:15:08.236 [INFO ]: saving elasticsearch entities: 46
    20180521T16:15:08.236 [INFO ]: execution complete: [Mon May 21 16:15:08 AEST 2018]
    20180521T16:15:08.236 [INFO ]: time taken (seconds): 2

    ```

1. Check the index to see how many partners you have:
   ```bash
   http localhost:9200/partner-records/partner-record/_search?size=0
   ```

   Response:
   ```bash
    HTTP/1.1 200 OK
    content-encoding: gzip
    content-length: 131
    content-type: application/json; charset=UTF-8

    {
        "_shards": {
            "failed": 0,
            "skipped": 0,
            "successful": 1,
            "total": 1
        },
        "hits": {
            "hits": [],
            "max_score": 0.0,
            "total": 1158
        },
        "timed_out": false,
        "took": 10
    }
    ```
    
    The sample above shows a total of 1158 records.
