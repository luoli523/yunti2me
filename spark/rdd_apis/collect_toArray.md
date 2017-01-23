## collect, toArray

将RDD转换成Scala的Array病返回。如果在调用时还提供了一个scala map函数（比如： f = T -> U），那么collect和toArray调用会将RDD中的数据进行map转换后的结果返回。

`注：这是一个action操作，会触发实际计算`

函数原型：

  def collect(): Array[T]
  def collect[U: ClassTag](f: PartialFunction[T, U]): RDD[U]
  def toArray(): Array[T]

例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog", "Gnu", "Rat"), 2)
c.collect
res29: Array[String] = Array(Gnu, Cat, Rat, Dog, Gnu, Rat)
```


