## 1. CK 测试集群 

```bash
clickhouse-client -m -uadmin --password admin123 -h clickhouse001.prod
clickhouse-client -m -uadmin --password admin123 -h clickhouse002.prod
```

### 1.1 hdfs_audit_log_all

* hdfs_audit_log_all schema

```sql
CREATE TABLE bigdata_infra.hdfs_audit_log_all
(
    `log_host` String,
    `ns` String,
    `message` String,
    `log_time` DateTime64(3),
    `log_level` String,
    `auth` String,
    `ugi` String,
    `ip` String,
    `cmd` String,
    `src` String,
    `dst` String,
    `perm` String,
    `proto` String,
    `filebeate_time` String,
    `create_time` DateTime DEFAULT now()
)
ENGINE = Distributed('cluster_2shards_1replicas', 'bigdata_infra', 'hdfs_audit_log_flink', rand())
```

* 查询hdfs audit明细

```sql
SELECT *
FROM bigdata_infra.hdfs_audit_log_all
WHERE (src = '/user/data_daedalus/.Trash/Current')
AND toDateTime(log_time) > '2020-08-13 00:00:00' AND toDateTime(log_time) < '2020-08-14 00:00:00'
AND ugi = 'hdfs'
ORDER BY log_time 
```

* hdfs audit查询按用户group by

```sql
SELECT ugi, COUNT(*) 
FROM bigdata_infra.hdfs_audit_log_all 
WHERE toDateTime(log_time) > '2020-08-17 00:00:00' AND toDateTime(log_time) < '2020-08-18 00:00:00'
GROUP BY ugi 
ORDER BY COUNT(*) DESC
```

