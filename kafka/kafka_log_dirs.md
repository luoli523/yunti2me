# kafka log dirs tool

当对集群进行管理时，常常需要查看某一个（或多个）topics在某些brokers上的数据存储的详细信息，此时可以使用`kafka-log-dirs.sh`工具来获取这些详细信息：

```shell
$ kafka-log-dirs --describe --bootstrap-server hostname:port --broker-list broker1,broker2 --topic-list topic1,topic2
```

* 如果没有提供broker-list，则会输出所有brokers的相关信息
* 如果没有提供topic-list，则会输出集群所有topics的相关信息

比如：

```shell
$ bin/kafka-log-dirs.sh --bootstrap-server localhost:9093  --describe
Querying brokers for log directories information
Received log directory information from brokers 0,1,2,3
{"version":1,"brokers":[{"broker":0,"logDirs":[{"logDir":"/Users/li.luo/dev/kafka-current/kafka-logs","error":null,"partitions":[{"partition":"__consumer_offsets-13","size":1012,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-46","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-9","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-42","size":2027,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-21","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-17","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-30","size":930,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-26","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-5","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-38","size":4508,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-1","size":1012,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-34","size":3859,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-16","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-45","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-12","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-41","size":0,"offsetLag":0,"isFuture":false},{"partition":"test-2","size":148,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-24","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-20","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-49","size":4954,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-0","size":1812,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-29","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-25","size":0,"offsetLag":0,"isFuture":false},{"partition":"tpch.lineitem-0","size":227588833423,"offsetLag":0,"isFuture":false},{"partition":"tpch.region-0","size":770,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-8","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-37","size":929,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-4","size":1207,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-33","size":930,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-15","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-48","size":0,"offsetLag":0,"isFuture":false},{"partition":"test-1","size":214,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-11","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-44","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-23","size":5414,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-19","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-32","size":1783,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-28","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-7","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-40","size":921,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-3","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-36","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-47","size":684,"offsetLag":0,"isFuture":false},{"partition":"tpch.nation-0","size":4200,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-14","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-43","size":0,"offsetLag":0,"isFuture":false},{"partition":"test-0","size":145,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-10","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-22","size":2236,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-18","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-31","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-27","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-39","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-6","size":0,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-35","size":6747,"offsetLag":0,"isFuture":false},{"partition":"__consumer_offsets-2","size":0,"offsetLag":0,"isFuture":false}]}]},{"broker":1,"logDirs":[{"logDir":"/Users/li.luo/dev/kafka-current/kafka-logs-1","error":null,"partitions":[{"partition":"tpch.customer-0","size":4595430228,"offsetLag":0,"isFuture":false}]}]},{"broker":2,"logDirs":[{"logDir":"/Users/li.luo/dev/kafka-current/kafka-logs-2","error":null,"partitions":[{"partition":"tpch.orders-0","size":42112446680,"offsetLag":0,"isFuture":false},{"partition":"test-1","size":214,"offsetLag":0,"isFuture":false},{"partition":"test-0","size":145,"offsetLag":0,"isFuture":false},{"partition":"test-2","size":148,"offsetLag":0,"isFuture":false}]}]},{"broker":3,"logDirs":[{"logDir":"/Users/li.luo/dev/kafka-current/kafka-logs-3","error":null,"partitions":[{"partition":"tpch.part-0","size":5259522156,"offsetLag":0,"isFuture":false},{"partition":"test-1","size":214,"offsetLag":0,"isFuture":false},{"partition":"test-0","size":145,"offsetLag":0,"isFuture":false},{"partition":"test-2","size":148,"offsetLag":0,"isFuture":false},{"partition":"tpch.partsupp-0","size":20794366172,"offsetLag":0,"isFuture":false},{"partition":"tpch.supplier-0","size":265628831,"offsetLag":0,"isFuture":false}]}]}]}
```

如果只想要查看某个（几个）topics在某几台brokers上的信息，在`--broker-list`和`--topic-list`中提供相应的topic列表和brokers列表即可：

```shell
$ bin/kafka-log-dirs.sh --bootstrap-server localhost:9093  --describe --broker-list 0,1,2,3 --topic-list tpch.customer,tpch.partsupp
Querying brokers for log directories information
Received log directory information from brokers 0,1,2,3
{
  "version": 1,
  "brokers": [
    {
      "broker": 0,
      "logDirs": [
        {
          "logDir": "/Users/li.luo/dev/kafka-current/kafka-logs",
          "error": null,
          "partitions": []
        }
      ]
    },
    {
      "broker": 1,
      "logDirs": [
        {
          "logDir": "/Users/li.luo/dev/kafka-current/kafka-logs-1",
          "error": null,
          "partitions": [
            {
              "partition": "tpch.customer-0",
              "size": 4595430228,
              "offsetLag": 0,
              "isFuture": false
            }
          ]
        }
      ]
    },
    {
      "broker": 2,
      "logDirs": [
        {
          "logDir": "/Users/li.luo/dev/kafka-current/kafka-logs-2",
          "error": null,
          "partitions": []
        }
      ]
    },
    {
      "broker": 3,
      "logDirs": [
        {
          "logDir": "/Users/li.luo/dev/kafka-current/kafka-logs-3",
          "error": null,
          "partitions": [
            {
              "partition": "tpch.partsupp-0",
              "size": 20794366172,
              "offsetLag": 0,
              "isFuture": false
            }
          ]
        }
      ]
    }
  ]
}
```

