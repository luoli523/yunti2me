# Spark Best Practice

## 能用reduceByKey就尽量不要用groupByKey

以下两种计算wordcount的方式都能够work，但是效率有差别：

```scala
val words = Array("one", "two", "two", "three", "three", "three")
val wordPairsRDD = sc.parallelize(words).map(word => (word, 1))

val wordCountsWithReduce = wordPairsRDD
  .reduceByKey(_ + _)
  .collect()

val wordCountsWithGroup = wordPairsRDD
  .groupByKey()
  .map(t => (t._1, t._2.sum))
  .collect()
```

这两种解决方案都能够计算得到正确的结果，但当数据量很大的时候，`reduceByKey`的效率要明显高于`groupByKey`。这是因为使用`reduceByKey`时，`Spark`能够知道在shuffle之前先将相同key的value进行一次map端的combine。这样能够大量减少数据shuffle产生的网络交换的数据量。

`reduceByKey`和`groupByKey`的两种计算方式如下两个图所示。当同一个partition中的数据在同一台机器上被处理的时候，`reduceByKey`能够在数据shuffle前先进行combine，然后shuffle的数据就是一家经过初步计算了的结果，而不是最原始的中间结果。

![image](../images/reduce_by_key.png)


而相反的，当调用`groupByKey`时，由于`groupByKey`函数并不知道用户对数据进行groupBy后想要进行何种操作（而`reduceByKey`是知道的，比如(_+_))，因此groupByKey函数只能将所有的中间结果KV对进行shuffle。

为了决定数据要shuffle到哪台机器上去，Spark通过对key进行一次partition函数的调用。当executor内存不足的时候，Spark会将一部分数据spill到磁盘上。但是，spark往磁盘一次flush最小是按照一个key，所有当一个key对应了大量的value的时候，会对内存和磁盘都产生影响。虽然Spark中可能后续会对这种情况进行处理，但这样做仍然是需要避免的。当spark需要spill数据到磁盘的时候，性能就会受到比较大的影响。

![image](../images/group_by_key.png)


当处理的数据量越大的时候，reduceByKey 和 groupByKey这两种方式的区别就越明显。

另外，不仅仅是reduceByKey，还有其他类似的函数也要优于groupByKey，例如：

* combineByKey
* foldByKey

它们都是在shuffle之前的map端就已经知道reduce端要对values进行的操作是哪一种，于是可以提前进行combine
