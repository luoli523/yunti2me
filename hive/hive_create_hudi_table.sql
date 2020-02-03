create database if not exists ods;

drop table ods.mysql_events;

create table if not exists ods.mysql_events(
  `database` string,
  `table` string,
  `type` string,
  ts bigint,
  position string,
  `data` string,
  old string,
  `def` string,
  `commit` boolean,
  xid bigint,
  xoffset bigint
)
partitioned by (replica_server_id int, server_id bigint, grass_date string, `hour` int)
ROW FORMAT SERDE 'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe'
STORED AS INPUTFORMAT 'com.uber.hoodie.hadoop.HoodieInputFormat'
OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'
LOCATION '/products/ods/mysql_events'
;
