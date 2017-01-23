## intersection

返回两个RDD的交集

函数原型：

  def intersection(other: RDD[T], numPartitions: Int): RDD[T]
  def intersection(other: RDD[T], partitioner: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]
  def intersection(other: RDD[T]): RDD[T]

例子：

```scala
val x = sc.parallelize(1 to 20)
val y = sc.parallelize(10 to 30)
val z = x.intersection(y)

z.collect
res74: Array[Int] = Array(16, 12, 20, 13, 17, 14, 18, 10, 19, 15, 11)
```


