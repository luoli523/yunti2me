## keyBy

对RDD item执行函数操作，生成KV pairs，返回KV对的RDD。对item执行函数的返回值为key，item本身的value为value。

函数原型：

def keyBy[K](f: T => K): RDD[(K, T)]

例子：

```scala

val a = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val b = a.keyBy(_.length)
b.collect
res26: Array[(Int, String)] = Array((3,dog), (6,salmon), (6,salmon), (3,rat), (8,elephant))
```


