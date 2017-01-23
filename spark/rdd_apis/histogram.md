## histogram [Double]
  
These functions take an RDD of doubles and create a histogram with either even spacing (the number of buckets equals to bucketCount) or arbitrary spacing based on  custom bucket boundaries supplied by the user via an array of double values. The result type of both variants is slightly different, the first function will return a tuple consisting of two arrays. The first array contains the computed bucket boundary values and the second array contains the corresponding count of values (i.e. the histogram). The second variant of the function will just return the histogram as an array of integers.
该函数take一个包含double items的RDD，并生成一个间隔均匀的矩形图（bucket数几位用户提供的bucketCount）或者生成由用户提供的double values array指定的间隔。

函数原型：

  def histogram(bucketCount: Int): Pair[Array[Double], Array[Long]]
  def histogram(buckets: Array[Double], evenBuckets: Boolean = false): Array[Long]

例子：

```scala

val a = sc.parallelize(List(1.1, 1.2, 1.3, 2.0, 2.1, 7.4, 7.5, 7.6, 8.8, 9.0), 3)
a.histogram(5)
res11: (Array[Double], Array[Long]) = (Array(1.1, 2.68, 4.26, 5.84, 7.42, 9.0),Array(5, 0, 0, 1, 4))

val a = sc.parallelize(List(9.1, 1.0, 1.2, 2.1, 1.3, 5.0, 2.0, 2.1, 7.4, 7.5, 7.6, 8.8, 10.0, 8.9, 5.5), 3)
a.histogram(6)
res18: (Array[Double], Array[Long]) = (Array(1.0, 2.5, 4.0, 5.5, 7.0, 8.5, 10.0),Array(6, 0, 1, 1, 3, 4))
```

Example with custom spacing

```scala
val a = sc.parallelize(List(1.1, 1.2, 1.3, 2.0, 2.1, 7.4, 7.5, 7.6, 8.8, 9.0), 3)
a.histogram(Array(0.0, 3.0, 8.0))
res14: Array[Long] = Array(5, 3)

val a = sc.parallelize(List(9.1, 1.0, 1.2, 2.1, 1.3, 5.0, 2.0, 2.1, 7.4, 7.5, 7.6, 8.8, 10.0, 8.9, 5.5), 3)
a.histogram(Array(0.0, 5.0, 10.0))
res1: Array[Long] = Array(6, 9)

a.histogram(Array(0.0, 5.0, 10.0, 15.0))
res1: Array[Long] = Array(6, 8, 1)
```

