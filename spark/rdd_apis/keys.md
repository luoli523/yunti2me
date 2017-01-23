## keys [Pair]

提取KV RDD中的所有key生成新的RDD

函数原型：

  def keys: RDD[K]

例子：

```scala

val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val b = a.map(x => (x.length, x))
b.keys.collect
res2: Array[Int] = Array(3, 5, 4, 3, 7, 5)
```


