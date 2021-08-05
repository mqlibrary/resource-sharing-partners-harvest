# Creating the Elasticsearch Indexes

By default, the only index that you need to create manually is the __partner-configs__ index as you need to populate this with:
1. Your institution's configuration.
1. Your Outlook email account access tokens for OAuth2 based authentication.

The other two indexes are, by default, created automatically by the harvester. However, in the event that you need to override the default settings for the index (just the shard and replica settings), you will need to manually download, edit and create the indexes. We will cover that process in this document.

The three indexes that we use in the architecture are already [covered in detail](https://github.com/mqlibrary/resource-sharing-partners-sync#elasticsearch).

To summarize:
1. __partner-configs__

    This index contains configuration information for the harvester and sync apps. This includes information on how you'd like your resource sharing partners configured in your Alma institution.

1. __partner-changes__

    This index contains change records for data that changes both from a source system (one of LADD, Tepuna, ILRS or Outlook) and from your Alma institution as well.

1. __partner-records__

    This index contains all partner record data from the various sources combined. The data from here, combined with the your institutions configuration in the __partner-configs__ index forms the data for the record that is updated in your Alma institution.

The mapping files for each of the indexes are in the [github repository](https://github.com/mqlibrary/resource-sharing-partners-harvest):
1. [__partner-configs__](https://raw.githubusercontent.com/mqlibrary/resource-sharing-partners-harvest/master/src/main/resources/mapping-partner-configs.json)
1. [__partner-changes__](https://raw.githubusercontent.com/mqlibrary/resource-sharing-partners-harvest/master/src/main/resources/mapping-partner-changes.json)
1. [__partner-records__](https://raw.githubusercontent.com/mqlibrary/resource-sharing-partners-harvest/master/src/main/resources/mapping-partner-records.json)


Lets take a look at the _partner-changes.json_ file and go through the structure.

```json
{
  "settings" : {
    "index" : {
      "number_of_shards" : 1, 
      "number_of_replicas" : 0
    }
  },
  "mappings": {
    "properties": {
      "time": { "type": "date", "format": "date_time_no_millis" },
      "source_system": { "type": "keyword"},
      "nuc": { "type": "keyword"},
      "field": { "type": "keyword" },
      "before": { "type": "text" },
      "after": { "type": "text" }
    }
  }
}```

The only changes that you'd typically make are to the _number_of_shards_ and _number_of_replicas_ settings. If you have two or more nodes in your elasticsearch cluster, the default settings are fine and you do not need to make any changes or create the __partner-changes__ or __partner-records__ indexes.

The default settings of _number_of_shards_ and _number_of_replicas_ causes 'minor' issues in a single node cluster. A single node can't have a replica shard created so the index is in a 'yellow' state - waiting on a new node to come online so it can create an additional replica shard.

For a single node cluster, it is advisable to set _number_of_replicas_ to __0__ and to do this you will need to manually edit a copy of the json file and use it to create the mapping.

Take a copy of the mapping (you can use the links above) and edit the _number_of_replicas_ field to be __0__. Save the file. In this example we will assume you are using __mapping-partner-changes.json__.

```json
{
  "settings" : {
    "index" : {
      "number_of_shards" : 1,
      "number_of_replicas" : 0
    }
  },
  "mappings": {
    "partner-change": { 
      "properties": {
        "time": { "type": "date", "format": "date_time_no_millis" },
        "source_system": { "type": "keyword"},
        "nuc": { "type": "keyword"},
        "field": { "type": "keyword" },
        "before": { "type": "text" },
        "after": { "type": "text" }
      }
    }
  }
}
```

To interface with the Elasticsearch cluster, we will be using [HTTPie](https://httpie.org/).

```bash
http PUT localhost:9200/partner-changes @/path/to/mapping-partner-changes.json
```

You should hopefully see something like:
```bash
HTTP/1.1 200 OK
content-encoding: gzip
content-length: 82
content-type: application/json; charset=UTF-8

{
    "acknowledged": true,
    "index": "partner-changes",
    "shards_acknowledged": true
}
```

Do the same for the other two indexes.

```bash
http PUT localhost:9200/partner-configs @/path/to/mapping-partner-configs.json
http PUT localhost:9200/partner-records @/path/to/mapping-partner-records.json
```

You can have a look at the status of your indexes with the following command:
```bash
http localhost:9200/_cat/indices?v
```

Hopefully you will see something like this:
```bash
HTTP/1.1 200 OK
content-encoding: gzip
content-length: 226
content-type: text/plain; charset=UTF-8

health status index           uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   partner-configs _rLGAwQrStC1RIW1BFJC6Q   1   0          0            0       230b           230b
green  open   partner-records dZEvupYlRDi6X8wHajE7gw   1   0          0            0       230b           230b
green  open   partner-changes xVea_Ov1TGGu11wE6GNdOA   1   0          0            0       230b           230b
```
