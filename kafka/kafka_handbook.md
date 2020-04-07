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

  > A string that is either "delete" or "compact" or both. This string designates the retention policy to use on old log segments. The default policy ("delete") will discard old segments when their retention time or size limit has been reached. The "compact" setting will enable log compaction on the topic.
  >
  > Type: list
  > Default: delete
  > Valid Values: [compact, delete]
  > Server Default Property: log.cleanup.policy
  > Importance: medium

  

  ```
  A string that is either "delete" or "compact" or both. This string designates the retention policy to use on old log segments. The default policy ("delete") will discard old segments when their retention time or size limit has been reached. The "compact" setting will enable log compaction on the topic.
  
  Type: list
  Default: delete
  Valid Values: [compact, delete]
  Server Default Property: log.cleanup.policy
  Importance: medium
  ```

  

* 



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

