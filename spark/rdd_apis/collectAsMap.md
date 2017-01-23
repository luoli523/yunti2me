## collectAsMap [Pair] 


跟`collect`函数相似，不过是用来将KV结构的RDD转换成scala map并返回

函数原型：

  def collectAsMap(): Map[K, V]

例子：

```scala

val a = sc.parallelize(List(1, 2, 1, 3), 1)
val b = a.zip(a)
b.collectAsMap
res1: scala.collection.Map[Int,Int] = Map(2 -> 2, 1 -> 1, 3 -> 3)
```


