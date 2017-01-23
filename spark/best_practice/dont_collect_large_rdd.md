# Spark Best Practice

##  不要collect大RDD到Driver

如果RDD的数据量很大，其所有的partition数据量无法在内存中全部装载的时候，尽量不要调用以下函数：

```scala
val values = myVeryLargeRDD.collect()
```

collect函数会将RDD中所有的item全部拷贝到Driver中，如果RDD数据量很大，很容易引起Driver直接就OOM了。

如果想要查看一些RDD的数据，可以使用`take`或者`takeSample`操作，或者`filter`或者`sample`一下RDD中的数据，只输出其中的一小部分。

同样的，不仅仅是collect，还有其他的几个操作也要小心类似的问题，比如：

* countByKey
* countByValue
* collectAsMap

如果实在需要查看这些数据的全部内容，可以先将这些数据输出到持久化存储中，比如HDFS，然后在去查看。
