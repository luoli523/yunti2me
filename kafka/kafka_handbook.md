# kafka handbook

## 1. topic

### 1.1 topic相关操作

* #### 创建topic

  ```bash
  # 创建一个包含1个partition，每个partition只有1个replica(replication-factor)的topic
  > bin/kafka-topics.sh --create \
                        --zookeeper localhost:2181 \
                        --replication-factor 1 
                        --partitions 1 \
                        --topic test
  
  # 创建一个包含1个partition，每个partition只有3个replica(replication-factor)的topic
  > bin/kafka-topics.sh --create \
                        --zookeeper localhost:2181 \
                        --replication-factor 3 \
                        --partitions 1 \
                        --topic my-replicated-topic
  ```

* #### list topic

  ```bash
  > bin/kafka-topics.sh --zookeeper localhost:9092 \
                        --list
  test
  ```

* #### describe topic

  ```bash
  # describe一个包含3个partition，每个partition有3个replica（replication-factor）的topic
  > bin/kafka-topics.sh --zookeeper localhost:2181 \
                        --describe \
                        --topic test
  Topic:test	PartitionCount:3	ReplicationFactor:3	Configs:
  	Topic: test	Partition: 0	Leader: 0	Replicas: 0,1,2	Isr: 0,1,2
  	Topic: test	Partition: 1	Leader: 0	Replicas: 0,1,2	Isr: 0,1,2
  	Topic: test	Partition: 2	Leader: 0	Replicas: 0,1,2	Isr: 0,1,2
  ```

### 1.2 topic配置选项操作

Topic相关的配置选项可以在server的config中进行默认设置，当客户端发送命令的时候没有提供相应的配置选项的特殊设定，则会使用server端的默认值。

客户端提供相关选项的设置方式是通过`--config` 选项来设置，如下：

```bash
> bin/kafka-topics.sh --bootstrap-server localhost:9092 \
                      --create \
                      --topic my-topic \
                      --partitions 1 \
                      --replication-factor 1 \
                      --config max.message.bytes=64000 \
                      --config flush.messages=1
```

如果需要通过客户端对某些选项进行修改，则使用`kafka-configs.sh`的`--alter` 的`--add-config`设置：

```bash
> bin/kafka-configs.sh --zookeeper localhost:2181 \
                       --entity-type topics \
                       --entity-name my-topic \
                       --alter \
                       --add-config max.message.bytes=128000
```

确认修改是否生效可以用`--describe`如下方式进行查看：

```bash
> bin/kafka-configs.sh --zookeeper localhost:2181 \
                       --entity-type topics \
                       --entity-name my-topic \
                       --describe
```

如果需要对某些选项的设置进行删除，用`--delete-config`的方式，如下：

```bash
> bin/kafka-configs.sh --zookeeper localhost:2181  \
                       --entity-type topics \
                       --entity-name my-topic \
                       --alter \
                       --delete-config max.message.bytes
```

### 1.3 topic相关配置选项

kafka中的topic默认都有这些配置选项，如果没有单独做特殊设定，则会使用系统默认值。

* **cleanup.policy**

  ```shell
  A string that is either "delete" or "compact" or both. This string designates the retention policy to use on old log segments. 
  The default policy ("delete") will discard old segments when their retention time or size limit has been reached. 
  The "compact" setting will enable log compaction on the topic.
  
  * Type: list
  * Default: delete
  * Valid Values: [compact, delete]
  * Server Default Property: log.cleanup.policy
  * Importance: medium
  ```

* **compression.type**

```shell
Specify the final compression type for a given topic. 
This configuration accepts the standard compression codecs ('gzip', 'snappy', 'lz4', 'zstd'). 
It additionally accepts 'uncompressed' which is equivalent to no compression; 
and 'producer' which means retain the original compression codec set by the producer.

* Type: string
* Default: producer
* Valid Values: [uncompressed, zstd, lz4, snappy, gzip, producer]
* Server Default Property: compression.type
* Importance: medium
```

* **delete.retention.ms**

```shell
The amount of time to retain delete tombstone markers for log compacted topics. 
This setting also gives a bound on the time in which a consumer must complete a read 
if they begin from offset 0 to ensure that they get a valid snapshot of the final stage 
(otherwise delete tombstones may be collected before they complete their scan).

* Type: long
* Default: 86400000
* Valid Values: [0,...]
* Server Default Property: log.cleaner.delete.retention.ms
* Importance: medium
```

* **file.delete.delay.ms**

```shell
The time to wait before deleting a file from the filesystem

* Type: long
* Default: 60000
* Valid Values: [0,...]
* Server Default Property: log.segment.delete.delay.ms
* Importance: medium
```

* **flush.messages**

```SHELL
This setting allows specifying an interval at which we will force an fsync of data written to the log. 
For example if this was set to 1 we would fsync after every message; 
if it were 5 we would fsync after every five messages. 
In general we recommend you not set this and use replication for durability and allow the operating system's background flush capabilities 
as it is more efficient. This setting can be overridden on a per-topic basis (see the per-topic configuration section).

* Type: long
* Default: 9223372036854775807
* Valid Values: [0,...]
* Server Default Property: log.flush.interval.messages
* Importance: medium
```

* **flush.ms**

```shell
This setting allows specifying a time interval at which we will force an fsync of data written to the log. 
For example if this was set to 1000 we would fsync after 1000 ms had passed. 
In general we recommend you not set this and use replication for durability and allow the operating system's background flush capabilities as it is more efficient.

* Type: long
* Default: 9223372036854775807
* Valid Values: [0,...]
* Server Default Property: log.flush.interval.ms
* Importance: medium
```

* **follower.replication.throttled.replicas**

```shell
A list of replicas for which log replication should be throttled on the follower side. 
The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... 
or alternatively the wildcard '*' can be used to throttle all replicas for this topic.

* Type: list
* Default: ""
* Valid Values: [partitionId]:[brokerId],[partitionId]:[brokerId],...
* Server Default Property: follower.replication.throttled.replicas
* Importance: medium
```

* **index.interval.bytes**

```shell
This setting controls how frequently Kafka adds an index entry to its offset index. 
The default setting ensures that we index a message roughly every 4096 bytes. 
More indexing allows reads to jump closer to the exact position in the log but makes the index larger. 
You probably don't need to change this.

* Type: int
* Default: 4096
* Valid Values: [0,...]
* Server Default Property: log.index.interval.bytes
* Importance: medium

```

* **leader.replication.throttled.replicas**

```shell
A list of replicas for which log replication should be throttled on the leader side. 
The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... 
or alternatively the wildcard '*' can be used to throttle all replicas for this topic.

* Type: list
* Default: ""
* Valid Values: [partitionId]:[brokerId],[partitionId]:[brokerId],...
* Server Default Property: leader.replication.throttled.replicas
* Importance: medium
```

* **max.compaction.lag.ms**

```shell
The maximum time a message will remain ineligible for compaction in the log. 
Only applicable for logs that are being compacted.

* Type: long
* Default: 9223372036854775807
* Valid Values: [1,...]
* Server Default Property: log.cleaner.max.compaction.lag.ms
* Importance: medium
```

* **max.message.bytes**

```shell
The largest record batch size allowed by Kafka. 
If this is increased and there are consumers older than 0.10.2, the consumers' fetch size must also be increased so that the they can fetch record batches this large. 
In the latest message format version, records are always grouped into batches for efficiency. 
In previous message format versions, uncompressed records are not grouped into batches and this limit only applies to a single record in that case.

* Type: int
* Default: 1000012
* Valid Values: [0,...]
* Server Default Property: message.max.bytes
* Importance: medium

```

* **message.format.version**

```shell
Specify the message format version the broker will use to append messages to the logs. 
The value should be a valid ApiVersion. Some examples are: 0.8.2, 0.9.0.0, 0.10.0, check ApiVersion for more details. 
By setting a particular message format version, the user is certifying that all the existing messages on disk are smaller or equal than the specified version. 
Setting this value incorrectly will cause consumers with older versions to break as they will receive messages with a format that they don't understand.

* Type: string
* Default: 2.4-IV1
* Valid Values: [0.8.0, 0.8.1, 0.8.2, 0.9.0, 0.10.0-IV0, 0.10.0-IV1, 0.10.1-IV0, 0.10.1-IV1, 0.10.1-IV2, 0.10.2-IV0, 0.11.0-IV0, 0.11.0-IV1, 0.11.0-IV2, 1.0-IV0, 1.1-IV0, 2.0-IV0, 2.0-IV1, 2.1-IV0, 2.1-IV1, 2.1-IV2, 2.2-IV0, 2.2-IV1, 2.3-IV0, 2.3-IV1, 2.4-IV0, 2.4-IV1]
* Server Default Property: log.message.format.version
* Importance: medium
```

* **message.timestamp.difference.max.ms**

```shell
The maximum difference allowed between the timestamp when a broker receives a message and the timestamp specified in the message. 
If message.timestamp.type=CreateTime, a message will be rejected if the difference in timestamp exceeds this threshold. 
This configuration is ignored if message.timestamp.type=LogAppendTime.

* Type: long
* Default: 9223372036854775807
* Valid Values: [0,...]
* Server Default Property: log.message.timestamp.difference.max.ms
* Importance: medium
```

* **message.timestamp.type**

```shell
Define whether the timestamp in the message is message create time or log append time. 
The value should be either `CreateTime` or `LogAppendTime`

* Type: string
* Default: CreateTime
* Valid Values: [CreateTime, LogAppendTime]
* Server Default Property: log.message.timestamp.type
* Importance: medium
```

* **min.cleanable.dirty.ratio**

```shell
This configuration controls how frequently the log compactor will attempt to clean the log (assuming log compaction is enabled). 
By default we will avoid cleaning a log where more than 50% of the log has been compacted. 
This ratio bounds the maximum space wasted in the log by duplicates (at 50% at most 50% of the log could be duplicates). 
A higher ratio will mean fewer, more efficient cleanings but will mean more wasted space in the log. 
If the max.compaction.lag.ms or the min.compaction.lag.ms configurations are also specified, 
then the log compactor considers the log to be eligible for compaction as soon as either: 
(i) the dirty ratio threshold has been met and the log has had dirty (uncompacted) records for at least the min.compaction.lag.ms duration, or 
(ii) if the log has had dirty (uncompacted) records for at most the max.compaction.lag.ms period.

* Type: double
* Default: 0.5
* Valid Values: [0,...,1]
* Server Default Property: log.cleaner.min.cleanable.ratio
* Importance: medium
```

* **min.compaction.lag.ms**

```shell
The minimum time a message will remain uncompacted in the log. 
Only applicable for logs that are being compacted.

* Type: long
* Default: 0
* Valid Values: [0,...]
* Server Default Property: log.cleaner.min.compaction.lag.ms
* Importance: medium
```

* **min.insync.replicas**

```shell
When a producer sets acks to "all" (or "-1"), this configuration specifies the minimum number 
of replicas that must acknowledge a write for the write to be considered successful. 
If this minimum cannot be met, then the producer will raise an exception (either 
NotEnoughReplicas or NotEnoughReplicasAfterAppend).
When used together, min.insync.replicas and acks allow you to enforce greater durability guarantees. 
A typical scenario would be to create a topic with a replication factor of 3, set 
min.insync.replicas to 2, and produce with acks of "all". 
This will ensure that the producer raises an exception if a majority of replicas 
do not receive a write.

* Type: int
* Default: 1
* Valid Values: [1,...]
* Server Default Property: min.insync.replicas
* Importance: medium

```

* **preallocate**

```shell
True if we should preallocate the file on disk when creating a new log segment.

* Type: boolean
* Default: false
* Valid Values:
* Server Default Property: log.preallocate
* Importance: medium
```

* **retention.ms**

```shell
This configuration controls the maximum time we will retain a log before we will discard old 
log segments to free up space if we are using the "delete" retention policy. 
This represents an SLA on how soon consumers must read their data. If set to -1, 
no time limit is applied.

* Type: long
* Default: 604800000  #(默认7天)
* Valid Values: [-1,...]
* Server Default Property: log.retention.ms
* Importance: medium
```

* **segment.bytes**

```shell
This configuration controls the segment file size for the log. 
Retention and cleaning is always done a file at a time so a larger segment size means fewer files but less granular control over retention.

* Type: int
* Default: 1073741824
* Valid Values: [14,...]
* Server Default Property: log.segment.bytes
* Importance: medium
```

* **segment.ms**

```shell
This configuration controls the period of time after which Kafka will force the log to roll 
even if the segment file isn't full to ensure that retention can delete or compact old data.

* Type: long
* Default: 604800000
* Valid Values: [1,...]
* Server Default Property: log.roll.ms
* Importance: medium
```

*  **unclean.leader.election.enable**

```shell
Indicates whether to enable replicas not in the ISR set to be elected as leader as a last resort, even though doing so may result in data loss.

* Type: boolean
* Default: false
* Valid Values:
* Server Default Property: unclean.leader.election.enable
* Importance: medium
```





## 2. consumer相关操作

* console consumer

  ```bash
  > bin/kafka-console-consumer.sh  --topic test \
                                   --bootstrap-server localhost:9094 \ #0.10.0及以后的版本这里可以直接提供一台或者多台的broker:port即可，kafka足够智能，知道去哪里找对应的meta信息。不需要写--zookeeper
                                   --from-beginning
  ```

## 3. producer相关操作

* console producer

```bash
> bin/kafka-console-producer.sh --broker-list localhost:9092 \
                                --topic test
This is a message
This is another message
```

## 4. 集群运维相关操作

* 启动单个kafka进程

  ```bash
  # 这里需要确保
  # 1, zookeeper服务(单点or集群)已经启动
  # 2, config/server.properties中zk相关的配置已经正确设置
  bin/kafka-server-start.sh config/server.properties 
  ```

* 启动多broker kafka集群

这里是用一台机器上不同端口的启动方式来模拟多台broker，在实际分布式环境中通常是所有kafka进程使用相同的端口

```bash
# 拷贝原config/server.properties，作为不同kafka进程启动的配置文件
> cp config/server.properties config/server-1.properties
> cp config/server.properties config/server-2.properties

# 修改两个配置文件中响应的几个配置选项，其中包括log目录（用来存储数据的目录，服务进程端口，以及broker的id）
config/server-1.properties:
    broker.id=1
    listeners=PLAINTEXT://:9093
    log.dirs=/tmp/kafka-logs-1
 
config/server-2.properties:
    broker.id=2
    listeners=PLAINTEXT://:9094
    log.dirs=/tmp/kafka-logs-2

# 然后用每个配置文件分别启动不同的kafka进程
> bin/kafka-server-start.sh config/server-1.properties &
...
> bin/kafka-server-start.sh config/server-2.properties &
...
```

