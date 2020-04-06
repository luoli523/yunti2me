# kafka handbook

## 1. topic相关

* 创建topic

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

* list topic

  ```bash
  > bin/kafka-topics.sh --zookeeper localhost:9092 \
                        --list
  test
  ```

* describe topic

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

  

## 2. consumer相关

* console consumer

  ```bash
  > bin/kafka-console-consumer.sh  --topic test \
                                   --bootstrap-server localhost:9094 \ #0.10.0及以后的版本这里可以直接提供一台或者多台的broker:port即可，kafka足够智能，知道去哪里找对应的meta信息。不需要写--zookeeper
                                   --from-beginning
  ```

## 3. producer相关

* console producer

```bash
> bin/kafka-console-producer.sh --broker-list localhost:9092 \
                                --topic test
This is a message
This is another message
```

## 4. 集群运维相关

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

