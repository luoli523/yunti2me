

## Kafka Quota

Kafka集群可以对client（consumer/producer）对broker的资源消耗进行quota控制。有两种类型的quota可以在broker上进行设置，以达到控制对一组client进行它们共享的quota的设置：

1. 通过对网络消耗进行阈值来对网络带宽的消耗进行控制（从0.9开始支持）
2. 通过对CPU的消耗阈值控制来对QPS进行控制（从0.11开始支持）

### User，Client-id，Group

Kafka客户端的身份是user，它代表安全集群中已通过身份验证的用户。Client-id一个很多clients的逻辑分组，通常对该分组进行一些带有标识含义的命名。而(user, client-id)组成的二元组就代表了一个clients的安全逻辑分组，该分组共享user账户和client-id。

Kafka的Quota可以被定义在(user, client-id)上，user上，或client-id groups上。给定一个connection，以上哪种quota设置匹配上了客户端标识就把quota按照匹配方式进行限制。在一个group中所有的connections共享一个quota设置。比如，如果(user="test-user", client-id="test-client") 被限制了10MB/sec的流量控制，则所有user为"test"并且client-id为"test-client"的group都共享该quota设置。

### Quota设置

Kafka的Quota可以被定义在(user, client-id)上，user上，或client-id groups上。可以在任何一个维度对默认的quota进行设置。机制类似于对单个topic的各种config设置。对user和 (user, client-id)的quota设置是被写入到zookeeper的`/config/users` 节点，对client-id的quota设置是被写入`/config/clients`节点中。这些zk节点中的配置设置会被所有的broker节点读取到，并实时的进行设置。这样就不需要对集群的任何服务进行重启，就能领quota实时生效。对每个group的quota动态设置也是用的相同的机制。

对quota设置的匹配实施是按照以下顺序：

1. /config/users/<user>/clients/<client-id>
2. /config/users/<user>/clients/<default>
3. /config/users/<user>
4. /config/users/<default>/clients/<client-id>
5. /config/users/<default>/clients/<default>
6. /config/users/<default>
7. /config/clients/<client-id>
8. /config/clients/<default>

Broker的`quota.producer.default`以及`quota.consumer.default`也可以用来对client-id group的网络带宽quota默认值进行设置。但这些设置将会在将来的版本中被去掉。

### 网络带宽quota设置

网络quota的设置是对每个client-id group进行设置，默认情况下每一个client group（client-id标识）的quota是由集群服务端的默认值控制。具体是反应在每个broker上的，每个client group的带宽上限默认受限于broker上对生产和消费的带宽默认quota设置。

### Request rate quota设置

Request Rate quota是由单位时间内一个client group能够对每个broker的request thread handler和网络threads数来反应的。比如一个n%的request rate的quota设置表示``((num.io.threads + num.network.threads) * 100)%`这么多比例的thread能够服务于该client group。



默认情况下，每一个client group都会有一个固定的quota配置，被设置在cluster的服务端。具体就是在各个broker的单独配置中。每一个client都会受限于每个broker的quota设置阈值。当一个broker发现quota超出阈值之后，broker就会计算出client需要被pending多少的时间，然后让该client进行hang up。如果是对消费客户端，那么此时不需要有任何数据返回，broker仅仅需要对client的请求sleep响应一段时间即可。对生产客户端，客户端也会在break quota上限后收到需要pending多长时间的response，然后客户端停止发送数据和消息直到sleep时间过去。即便是老版本的客户端无法对访问进行pending，broker服务端也能够通过停止响应的方式达到相同的控制效果。

### quota设置命令

可以通过`kafka-config.sh`工具来对user，client-id进行quota的设置，比如：

* 对`(user=user1, client-id=clientA)`进行设置

```shell
$ bin/kafka-configs.sh  --zookeeper localhost:2181 \
                        --alter \
                        --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048,request_percentage=200' \
                        --entity-type users \
                        --entity-name user1 \
                        --entity-type clients \
                        --entity-name client1
Updated config for entity: user-principal 'user1', client-id 'client1'.

# 可以从zk中看到相应的设置内容
ls /config
[changes, clients, brokers, topics, users]
ls /config/users
[user1]
ls /config/users/user1
[clients]
ls /config/users/user1/clients
[client1]
ls /config/users/user1/clients/client1
[]
get /config/users/user1/clients/client1
{"version":1,"config":{"producer_byte_rate":"1024","request_percentage":"200","consumer_byte_rate":"2048"}}

```

* 对`user=user2`进行设置

```shell
$ bin/kafka-configs.sh  --zookeeper localhost:2181 \
                        --alter \
                        --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048,request_percentage=200' \
                        --entity-type users \
                        --entity-name user2
Updated config for entity: user-principal 'user2'.

# 可以从zk中看到相应的设置内容
ls /config
[changes, clients, brokers, topics, users]
ls /config/users
[user1, user2]
ls /config/users/user2
[]
get /config/users/user2
{"version":1,"config":{"producer_byte_rate":"1024","request_percentage":"200","consumer_byte_rate":"2048"}}
```

* 对`client-id=clientB`进行设置

```shell
$ bin/kafka-configs.sh --zookeeper localhost:2181 \
                       --alter \
                       --entity-type clients \
                       --entity-name clientB \
                       --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048,request_percentage=200'
Completed Updating config for entity: client-id 'clientB'.

# 可以从zk中看到相应的设置内容
ls /config
[changes, clients, brokers, topics, users]
ls /config/clients
[clientB]
ls /config/clients/clientB
[]
get /config/clients/clientB
{"version":1,"config":{"producer_byte_rate":"1024","request_percentage":"200","consumer_byte_rate":"2048"}}
```

可以通过用`-entity-default`选项来对所有的user和client-id进行默认quota的设置：

* 为`user=userC`配置默认的 client-id quota:

```shell
$ bin/kafka-configs.sh  --zookeeper localhost:2181 \
                        --alter \
                        --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048,request_percentage=200' \
                        --entity-type users \
                        --entity-name user3
                        --entity-type clients \
                        --entity-default
Completed Updating config for entity: user-principal 'user3', default client-id.

# 可以从zk中看到相应的设置内容
ls /config
[changes, clients, brokers, topics, users]
ls /config/users
[user1, user2, user3]
ls /config/users/user3
[clients]
ls /config/users/user3/clients
[<default>]
ls /config/users/user3/clients/<default>
[]
get /config/users/user3/clients/<default>
{"version":1,"config":{"producer_byte_rate":"1024","request_percentage":"200","consumer_byte_rate":"2048"}}
```

* 为所有的user配置默认的quota

```shell
$ bin/kafka-configs.sh --zookeeper localhost:2181 \
                       --alter \
                       --entity-type users \
                       --entity-default \
                       --add-config 'producer_byte_rate=1024,consumer_byte_rate=2048,request_percentage=200'
Completed Updating config for entity: default user-principal.

# 可以从zk中看到相应的设置内容
ls /config
[changes, clients, brokers, topics, users]
ls /config/users
[user1, user2, <default>, user3]
ls /config/users/<default>
[]
get /config/users/<default>
{"version":1,"config":{"producer_byte_rate":"1024","request_percentage":"200","consumer_byte_rate":"2048"}}
```

* 检查设置

除了通过查看zk节点的方式来检查设置外，也可以通过`kafka-config.sh`命令的`--describe`选项来查看设置：

```shell
$ bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type clients
Configs for client-id 'clientB' are request_percentage=200,producer_byte_rate=1024,consumer_byte_rate=2048

$ bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type clients --entity-type users
Configs for user-principal 'user1', client-id 'client1' are request_percentage=200,producer_byte_rate=1024,consumer_byte_rate=2048
Configs for user-principal 'user3', default client-id are request_percentage=200,producer_byte_rate=1024,consumer_byte_rate=2048

$ bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type clients --entity-type users --entity-name user3
Configs for user-principal 'user3', default client-id are request_percentage=200,producer_byte_rate=1024,consumer_byte_rate=2048
```

在broker服务端也可以对users和clients进行默认的quota设置，默认如果不做设置，那么client是不受任何限制。以下设置分别对生产者和消费者的网络quota设置了10MB/sec的上限。

```properties
quota.producer.default=10485760
quota.consumer.default=10485760
```