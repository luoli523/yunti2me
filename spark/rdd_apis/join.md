## join [Pair]

对两个KV RDD进行inner Join操作。

注意：

* 两个RDD的key都必须是comparable的类型

函数原型：

  def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
  def join[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (V, W))]
  def join[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (V, W))]

例子：

```scala

val a = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val b = a.keyBy(_.length)
val c = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val d = c.keyBy(_.length)
b.join(d).collect

res0: Array[(Int, (String, String))] = Array((6,(salmon,salmon)), (6,(salmon,rabbit)), (6,(salmon,turkey)), (6,(salmon,salmon)), (6,(salmon,rabbit)), (6,(salmon,turkey)), (3,(dog,dog)), (3,(dog,cat)), (3,(dog,gnu)), (3,(dog,bee)), (3,(rat,dog)), (3,(rat,cat)), (3,(rat,gnu)), (3,(rat,bee)))
```


