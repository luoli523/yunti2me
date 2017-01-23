## distinct
  
返回一个该RDD中每一个item仅仅包含一次的新RDD

`注：这是一个action操作，会触发实际计算`

函数原型：

  def distinct(): RDD[T]
  def distinct(numPartitions: Int): RDD[T]

例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog", "Gnu", "Rat"), 2)
c.distinct.collect
res6: Array[String] = Array(Dog, Gnu, Cat, Rat)

val a = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10))
a.distinct(2).partitions.length
res16: Int = 2

a.distinct(3).partitions.length
res17: Int = 3
```

