## mapValues [Pair]

将RDD中每个key的values中的items参数执行map函数，并返回key和转换后的输出value到新的RDD中。

函数原型：

  def mapValues[U](f: V => U): RDD[(K, U)]

例子：

```scala

val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val b = a.map(x => (x.length, x))
b.mapValues("x" + _ + "x").collect
res5: Array[(Int, String)] = Array((3,xdogx), (5,xtigerx), (4,xlionx), (3,xcatx), (7,xpantherx), (5,xeaglex))
```


