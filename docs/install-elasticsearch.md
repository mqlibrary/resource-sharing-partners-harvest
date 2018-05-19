# Installing Elasticsearch

Elasticsearch can be downloaded from the following URL:
https://www.elastic.co/downloads/elasticsearch

This document is using __elasticsearch-6.2.4.tar.gz__.

Installing a small instance of a single node Elasticsearch cluster will involve the following steps:

1. Logon as the root user (or prefix commands with 'sudo').
1. Set the __vm.max_map_count__ setting in the current context and the __/etc/sysctl.conf__ file.

    ```bash
    echo 'vm.max_map_count=262144' >> /etc/sysctl.conf
    sysctl vm.max_map_count=262144
    ```
1. Create an Elasticsearch user.

    ```bash
    useradd -c "Elasticsearch User" elastic
    ```
1. Create folder structure and change ownership.

    ```bash
    mkdir -p /opt/es/data
    mkdir -p /opt/es/logs
    mkdir -p /opt/es/backups
    chown -R elastic:elastic /opt/es
    ```
1. Extract the downloaded Elasticsearch package into /opt, create a symlink and change ownerships.

    ```bash
    cd /opt
    tar xzf /path/to/elasticsearch-6.2.4.tar.gz
    ln -s elasticsearch-6.2.4 elasticsearch
    chown -R elastic:elastic elasticsearch-6.2.4
    ```
1. Create/edit the elasticsearch configuration file and ensure the following are set: __/opt/elasticsearch/config/elasticsearch.yml__

    ```yaml
    cluster.name: esdata
    node.name: solo
    path.data: /opt/es/data
    path.logs: /opt/es/logs
    path.repo: /opt/es/backups
    network.host: ["_local_","_site_"]
    http.port: 9200
    ```
1. Create/edit the jvm parameters in the configuration file __/opt/elasticsearch/config/jvm.options__.
Set the memory requirements for the Elasticsearch instance. No more than half the available RAM available on a machine. You could use 256m as well.

    ```bash
    -Xms512m
    -Xmx512m
    ```
1. Create a systemd service (if your system uses systemd) by creating the following file: __/etc/systemd/system/elasticsearch.service__ with the following content:

    ```bash
    [Service]
    User=elastic
    Group=elastic
    LimitNOFILE=65536
    LimitNPROC=4096
    ExecStart=/opt/elasticsearch/bin/elasticsearch
    Restart=always
    RestartSec=10

    [Install]
    WantedBy=multi-user.target
    ```
1. Open the firewall (if using firewalld and the elasticsearch service is already in place)

    ```bash
    firewall-cmd --zone=public --add-service=elasticsearch --permanent
    firewall-cmd --zone=public --add-service=elasticsearch
    ```
1. Enable and start the elasticsearch service.

    ```bash
    systemctl enable elasticsearch
    systemctl start elasticsearch
    ```
1. View the startup logs and ensure all looks good:

    ```bash
    journalctl -efu elasticsearch
    ```
