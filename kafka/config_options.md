
=== broker ===

controlled.shutdown.enable=true
controlled.shutdown.max.retries=3
controlled.shutdown.retry.backoff.ms=5000

auto.leader.rebalance.enable=true
leader.imbalance.check.interval.seconds=300
leader.imbalance.per.broker.percentage=10
auto.create.topics.enable=false

delete.topic.enable=true

quota

=== produer ===

```properties
# the batch size in producer for every individual partition
batch.size  

# instruct the producer to wait up to that number of milliseconds to send the messages in batch
linger.ms    

# control the total amount of memory available to the producer for buffering
buffer.memory 

# when buffer space is exhausted additional send calls will block. The threshold for time to block is determined by this config after which it throws a TimeoutException
max.block.ms 

# instruct how to turn the key and value objects the user provides with their ProducerRecord into bytes. Can use the included ByteArraySerializer or StringSerializer for simple string or byte types.
key.serializer
value.serializer

# send messages in exactly once model.which means in this model the retries config will be 'Ingeter.MAX_VALUE' and the acks config will be  'all'
enable.idempotence
```







