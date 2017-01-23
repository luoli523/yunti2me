## aggregate


aggregate函数可以让用户对RDD进行两轮reduce函数操作。第一轮reduce函数作用在每个RDD partition内部，对每个RDD内的数据进行一轮reduce计算，产生一个结果。第二轮reduce函数作用在第一轮中每个partition上reduce函数的结果上，同样产生一个最终结果。这种用户自己提供两轮reduce计算逻辑的方式给用户提供了非常大的灵活性。例如第一轮reduce可以计算出各个partition中最大的值，而第二轮reduce函数可以计算这些最大值的和。不仅如此，用户还可以提供一个初始值，用来参与到两轮的reduce计算中。

aggregate函数需要注意以下几点：

* 初始值不仅仅会参与每个partition上的第一轮计算，同时也会参与第二轮的reduce计算。
* 两轮reduce的函数都要对各个item进行交替的组合计算
* 每一轮的reduce计算的顺序都是不一定的。

aggregate函数原型：

    def aggregate[U: ClassTag](zeroValue: U)(seqOp: (U, T) => U, combOp: (U, U) => U): U

来看例子

```scala
val z = sc.parallelize(List(1,2,3,4,5,6), 2)

// 先打印出各个item以及其在各个partition上的分布情况
def myfunc(index: Int, iter: Iterator[(Int)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}

// 可以看出123是分布在一个partition上，456分布在另外一个partition上
// 注：这里同时展示了mapPartitionsWithIndex函数的使用场景
z.mapPartitionsWithIndex(myfunc).collect
res28: Array[String] = Array([partID:0, val: 1], [partID:0, val: 2], [partID:0, val: 3], [partID:1, val: 4], [partID:1, val: 5], [partID:1, val: 6])

// RDD的aggregation函数需要提供三个参数
// 1, 一个初始值
// 2, 在每个partition中对各个item进行reduce的函数
// 3, 最后对所有partition上reduce后的结果进行再次reduce的函数
z.aggregate(0)(math.max(_, _), _ + _)
res40: Int = 9


// 这个例子的最终值是16的原因是：初始值是5
// 在partition 0上的reduce函数的结果是max(5, 1, 2, 3) = 5
// 在partition 0上的reduce函数的结果是max(5, 4, 5, 6) = 6
// 最后第二个reduce函数将初始值，以及各个partition上第一轮reduce的结果进行二次reduce的结果：5 + 5 + 6 = 16
// 需要注意，最终结果中需要再次包含初始值进行计算
z.aggregate(5)(math.max(_, _), _ + _)
res29: Int = 16


val z = sc.parallelize(List("a","b","c","d","e","f"),2)

// 查看各个item在各个partition上的分布情况
def myfunc(index: Int, iter: Iterator[(String)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}

z.mapPartitionsWithIndex(myfunc).collect
res31: Array[String] = Array([partID:0, val: a], [partID:0, val: b], [partID:0, val: c], [partID:1, val: d], [partID:1, val: e], [partID:1, val: f])

// 注意，这里结果也有可能是defabc,因为各个partition的结果在第二轮reduce的时候的顺序是不一定的
z.aggregate("")(_ + _, _+_)
res115: String = abcdef


// 看这里的初始值“x”被使用了三次
//  - 在每个partition上进行第一轮reduce的时候一次
//  - 最后对各个partition上的值进行第二轮reduce的时候第二次
// 注意这里的结果也有可能是“xxabcxdef”,原因同上
z.aggregate("x")(_ + _, _+_)
res116: String = xxdefxabc

// 下面是一些其他的用法, 有一些比较tricky.

val z = sc.parallelize(List("12","23","345","4567"),2)
z.aggregate("")((x,y) => math.max(x.length, y.length).toString, (x,y) => x + y)
res141: String = 42

z.aggregate("")((x,y) => math.min(x.length, y.length).toString, (x,y) => x + y)
res142: String = 11

val z = sc.parallelize(List("12","23","345",""),2)
z.aggregate("")((x,y) => math.min(x.length, y.length).toString, (x,y) => x + y)
res143: String = 10
```


## aggregateByKey [pair]


aggregateByKey是作用在[K,V]对的数据上的。它跟aggregate函数比较像，只不过reduce function并不是作用在所有的item上，而是作用在同一个key的所有item上。同时区别于aggregate，aggregateByKey初始值只作用于第一轮reduce，不参与第二轮reduce的计算。

函数原型：

    def aggregateByKey[U](zeroValue: U)(seqOp: (U, V) ⇒ U, combOp: (U, U) ⇒ U)(implicit arg0: ClassTag[U]): RDD[(K, U)]
    def aggregateByKey[U](zeroValue: U, numPartitions: Int)(seqOp: (U, V) ⇒ U, combOp: (U, U) ⇒ U)(implicit arg0: ClassTag[U]): RDD[(K, U)]
    def aggregateByKey[U](zeroValue: U, partitioner: Partitioner)(seqOp: (U, V) ⇒ U, combOp: (U, U) ⇒ U)(implicit arg0: ClassTag[U]): RDD[(K, U)]
    

来看例子：

```scala
val pairRDD = sc.parallelize(List( ("cat",2), ("cat", 5), ("mouse", 4),("cat", 12), ("dog", 12), ("mouse", 2)), 2)

// 先查看各个item以及其在各个partition上的分布情况
def myfunc(index: Int, iter: Iterator[(String, Int)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
pairRDD.mapPartitionsWithIndex(myfunc).collect

res2: Array[String] = Array([partID:0, val: (cat,2)], [partID:0, val: (cat,5)], [partID:0, val: (mouse,4)], [partID:1, val: (cat,12)], [partID:1, val: (dog,12)], [partID:1, val: (mouse,2)])

pairRDD.aggregateByKey(0)(math.max(_, _), _ + _).collect
res3: Array[(String, Int)] = Array((dog,12), (cat,17), (mouse,6))

pairRDD.aggregateByKey(100)(math.max(_, _), _ + _).collect
