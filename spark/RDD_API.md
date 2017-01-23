<!-- MarkdownTOC -->

- [RDD API][rdd-api]
	- [aggregate][aggregate]
	- [aggregateByKey][pair]
	- [cartesian][cartesian]
	- [checkpoint][checkpoint]
	- [coalesce, repartition][coalesce-repartition]
	- [cogroup Pair, groupWith][Pair]
	- [collect, toArray][collect-toarray]
	- [collectAsMap][Pair]
	- [combineByKey][Pair]
	- [compute][compute]
	- [context, sparkContext][context-sparkcontext]
	- [count][count]
	- [countByKey][countbykey]
	- [countByValue][countbyvalue]
	- [dependencies][dependencies]
	- [distinct][distinct]
	- [first][first]
	- [filter][filter]
	- [flatMap][flatmap]
	- [flatMapValues][flatmapvalues]
	- [fold][fold]
	- [foldByKey][Pair]
	- [foreach][foreach]
	- [foreachPartition][foreachpartition]
	- [fullOuterJoin][Pair]
	- [getCheckpointFile][getcheckpointfile]
	- [getStorageLevel][getstoragelevel]
	- [glom][glom]
	- [roupBy][roupby]
	- [groupByKey][Pair]
	- [histogram][Double]
	- [intersection][intersection]
	- [isCheckpointed][ischeckpointed]
	- [join][Pair]
	- [keyBy][keyby]
	- [keys][Pair]
	- [leftOuterJoin][Pair]
	- [map][map]
	- [mapPartitions][mappartitions]
	- [mapPartitionsWithIndex][mappartitionswithindex]
	- [mapValues][Pair]
	- [max][max]
	- [mean Double, meanApprox][Double]
	- [min][min]
	- [partitionBy][Pair]
	- [persist, cache][persist-cache]
	- [pipe][pipe]
	- [reduce][reduce]
	- [repartition][repartition]
	- [repartitionAndSortWithinPartitions][Ordered]
	- [rightOuterJoin][Pair]
	- [sortBy][sortby]
	- [sortByKey][Ordered]

<!-- /MarkdownTOC -->
# RDD API

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
res4: Array[(String, Int)] = Array((dog,100), (cat,200), (mouse,200))
```

## cartesian


笛卡尔积函数，计算两个RDD中数据的笛卡尔积（第一个RDD中的每一个item join 第二个RDD中的每一个item），并返回包含笛卡尔积数据结果的新RDD（Warning: 这个函数使用的时候需要慎重，因为会产生大量的中间结果数据，RDD消耗的内存会瞬间暴涨）

函数原型：

    def cartesian[U: ClassTag](other: RDD[U]): RDD[(T, U)]
    
来看例子：

```scala
val x = sc.parallelize(List(1,2,3,4,5))
val y = sc.parallelize(List(6,7,8,9,10))

x.cartesian(y).collect

res0: Array[(Int, Int)] = Array((1,6), (1,7), (1,8), (1,9), (1,10), (2,6), (2,7), (2,8), (2,9), (2,10), (3,6), (3,7), (3,8), (3,9), (3,10), (4,6), (5,6), (4,7), (5,7), (4,8), (5,8), (4,9), (4,10), (5,9), (5,10))
```


## checkpoint



checkpoint函数用来将RDD中的数据进行临时持久化存储，RDD的数据会被以二进制文件的形式存储到checkpoint dir中。

注意：由于spark的lazy evaluation机制，实际的checkpoint存储动作会直到RDD action操作真正发生的时候才会执行。

另：`my_directory_name` 默认是在slave的local，该目录需要在各个slave上都存在。或者用户可以将该目录指定为HDFS目录也可以。

函数原型

    def checkpoint()

例子：

```scala
sc.setCheckpointDir("my_directory_name")

val a = sc.parallelize(1 to 4)

a.checkpoint

// action发生的时候，才会真正执行checkpoint操作
a.count
14/02/25 18:13:53 INFO SparkContext: Starting job: count at <console>:15
...
14/02/25 18:13:53 INFO MemoryStore: Block broadcast_5 stored as values to memory (estimated size 115.7 KB, free 296.3 MB)
14/02/25 18:13:53 INFO RDDCheckpointData: Done checkpointing RDD 11 to file:/home/guili.ll/Documents/spark-0.9.0-incubating-bin-cdh4/bin/my_directory_name/65407913-fdc6-4ec1-82c9-48a1656b95d6/rdd-11, new parent is RDD 12
res23: Long = 4
```

## coalesce, repartition


coalesce和repartition都是用来对RDD数据进行重新partition分布的函数。

coalesce对RDD数据进行制定partition num的重新分配，repartition(numPartitions)是对coalesce(numPartitions, shuffle=true)的包装

函数原型

	def coalesce ( numPartitions : Int , shuffle : Boolean = false ): RDD [T]
	def repartition ( numPartitions : Int ): RDD [T]
	
例子：

```scala

val y = sc.parallelize(1 to 10, 10)
val z = y.coalesce(2, false)
z.partitions.length
res9: Int = 2
```

## cogroup [Pair], groupWith [Pair]


cogroup和groupWith都是作用在[K,V]结构的item上的函数，它们都是非常有用的函数，能够将不同RDD的相同key的values group到一起。

函数原型：

	def cogroup[W](other: RDD[(K, W)]): RDD[(K, (Iterable[V], Iterable[W]))]
	def cogroup[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (Iterable[V], Iterable[W]))]
	def cogroup[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (Iterable[V], Iterable[W]))]
	def cogroup[W1, W2](other1: RDD[(K, W1)], other2: RDD[(K, W2)]): RDD[(K, (Iterable[V], Iterable[W1], Iterable[W2]))]
	def cogroup[W1, W2](other1: RDD[(K, W1)], other2: RDD[(K, W2)], numPartitions: Int): RDD[(K, (Iterable[V], Iterable[W1], Iterable[W2]))]
	def cogroup[W1, W2](other1: RDD[(K, W1)], other2: RDD[(K, W2)], partitioner: Partitioner): RDD[(K, (Iterable[V], Iterable[W1], Iterable[W2]))]
	
	def groupWith[W](other: RDD[(K, W)]): RDD[(K, (Iterable[V], Iterable[W]))]
	def groupWith[W1, W2](other1: RDD[(K, W1)], other2: RDD[(K, W2)]): RDD[(K, (Iterable[V], IterableW1], Iterable[W2]))]
	
例子：

```scala
val a = sc.parallelize(List(1, 2, 1, 3), 1)
val b = a.map((_, "b"))
val c = a.map((_, "c"))

b.cogroup(c).collect
res7: Array[(Int, (Iterable[String], Iterable[String]))] = Array(
(2,(ArrayBuffer(b),ArrayBuffer(c))),
(3,(ArrayBuffer(b),ArrayBuffer(c))),
(1,(ArrayBuffer(b, b),ArrayBuffer(c, c)))
)


val d = a.map((_, "d"))

b.cogroup(c, d).collect
res9: Array[(Int, (Iterable[String], Iterable[String], Iterable[String]))] = Array(
(2,(ArrayBuffer(b),ArrayBuffer(c),ArrayBuffer(d))),
(3,(ArrayBuffer(b),ArrayBuffer(c),ArrayBuffer(d))),
(1,(ArrayBuffer(b, b),ArrayBuffer(c, c),ArrayBuffer(d, d)))
)


val x = sc.parallelize(List((1, "apple"), (2, "banana"), (3, "orange"), (4, "kiwi")), 2)
val y = sc.parallelize(List((5, "computer"), (1, "laptop"), (1, "desktop"), (4, "iPad")), 2)
x.cogroup(y).collect
res23: Array[(Int, (Iterable[String], Iterable[String]))] = Array(
(4,(ArrayBuffer(kiwi),ArrayBuffer(iPad))), 
(2,(ArrayBuffer(banana),ArrayBuffer())), 
(3,(ArrayBuffer(orange),ArrayBuffer())),
(1,(ArrayBuffer(apple),ArrayBuffer(laptop, desktop))),
(5,(ArrayBuffer(),ArrayBuffer(computer))))
```


## collect, toArray

将RDD转换成Scala的Array并返回。如果在调用时还提供了一个scala map函数（比如： f = T -> U），那么collect和toArray调用会将RDD中的数据进行map转换后的结果返回。

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

## combineByKey[Pair] 

Very efficient implementation that combines the values of a RDD consisting of two-component tuples by applying multiple aggregators one after another.


函数原型：

	def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C): RDD[(K, C)]
	def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numPartitions: Int): RDD[(K, C)]
	def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, partitioner: Partitioner, mapSideCombine: Boolean = true, serializerClass: String = null): RDD[(K, C)]
	
例子：

```scala

val a = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val b = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)
val c = b.zip(a)

// 第一个函数对每一个key，用来将key的第一个value初始化到一个特定的结构
// 第二个函数用来在每一个partition中进行对初始化结构和values中的每一个item进行reduce函数运算
// 第三个函数用来在对每一个partition上产生的结果按照相同的key再次按照第三个reduce函数进行计算
val d = c.combineByKey(List(_), (x:List[String], y:String) => y :: x, (x:List[String], y:List[String]) => x ::: y)

d.collect
res16: Array[(Int, List[String])] = Array((1,List(cat, dog, turkey)), (2,List(gnu, rabbit, salmon, bee, bear, wolf)))
```


## compute


执行dependencies并计算出最终的RDD。这个函数用户不需要主动调用。是一个developer API

函数原型

	def compute(split: Partition, context: TaskContext): Iterator[T]
	

## context, sparkContext

返回该RDD对应的SparkContext对象。

函数原型：

	def compute(split: Partition, context: TaskContext): Iterator[T]

例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog"), 2)
c.context
res8: org.apache.spark.SparkContext = org.apache.spark.SparkContext@58c1c2f1
```

## count

返回RDD中的item数

`注：这是一个action操作，会触发实际计算`

函数原型：

	def count(): Long
	
例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog"), 2)
c.count
res2: Long = 4
```


## countByKey

跟count很相似，不过是作用在item为[K,V]结构的RDD上，返回每一个key对应的values数的map

`注：这是一个action操作，会触发实际计算`

函数原型

	def countByKey(): Map[K, Long]
	
例子：

```scala


val c = sc.parallelize(List((3, "Gnu"), (3, "Yak"), (5, "Mouse"), (3, "Dog")), 2)
c.countByKey
res3: scala.collection.Map[Int,Long] = Map(3 -> 3, 5 -> 1)
```

## countByValue

返回一个map，该map的key是RDD中的item，value是该item出现过的次数。

`注：该操作会用一个reduce一次aggregate`

`注：这是一个action操作，会触发实际计算`

函数原型：

	def countByValue(): Map[T, Long]

例子：

```scala
val b = sc.parallelize(List(1,2,3,4,5,6,7,8,2,4,2,1,1,1,1,1))
b.countByValue
res27: scala.collection.Map[Int,Long] = Map(5 -> 1, 8 -> 1, 3 -> 1, 6 -> 1, 1 -> 6, 2 -> 3, 4 -> 2, 7 -> 1)
```


## dependencies

返回RDD的dependencies关系

函数原型

	final def dependencies: Seq[Dependency[_]]

例子：

```scala

val b = sc.parallelize(List(1,2,3,4,5,6,7,8,2,4,2,1,1,1,1,1))
b: org.apache.spark.rdd.RDD[Int] = ParallelCollectionRDD[32] at parallelize at <console>:12
b.dependencies.length
Int = 0

b.map(a => a).dependencies.length
res40: Int = 1

b.cartesian(a).dependencies.length
res41: Int = 2

b.cartesian(a).dependencies
res42: Seq[org.apache.spark.Dependency[_]] = List(org.apache.spark.rdd.CartesianRDD$$anon$1@576ddaaa, org.apache.spark.rdd.CartesianRDD$$anon$2@6d2efbbd)
```

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

## first
  
返回该RDD中的第一个元素

`注：这是一个action操作，会触发实际计算`

函数原型：

	def first(): T

例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog"), 2)
c.first
res1: String = Gnu
```

## filter
  
对每个RDD item执行一次参数函数，若返回true则collect到该item到新的RDD中。

函数原型：

	def filter(f: T => Boolean): RDD[T]

例子：

```scala

val a = sc.parallelize(1 to 10, 3)
val b = a.filter(_ % 2 == 0)
b.collect
res3: Array[Int] = Array(2, 4, 6, 8, 10)
```
当用户提供filter函数时，该filter函数必须能够处理所有包含在该RDD种的items。scala提供了partial functions来处理混合类型的数据。（partial function对于处理含有脏数据的结构非常有用，能够对脏数据进行特定处理的同时，对好的数据进行正常的map函数处理。）

混合数据类型without partial function的例子：

```scala
val b = sc.parallelize(1 to 8)
b.filter(_ < 4).collect
res15: Array[Int] = Array(1, 2, 3)

val a = sc.parallelize(List("cat", "horse", 4.0, 3.5, 2, "dog"))
a.filter(_ < 4).collect
<console>:15: error: value < is not a member of Any
```


这里失败的原因是因为在有一些item无法进行跟integer的比较操作。容器使用函数对象的isDefinedAt属性来判断该item是否适用于进行该函数的执行，只有返回true的item才会被用来进行map函数的计算。

混合数据类型partial function的例子：

```scala
val a = sc.parallelize(List("cat", "horse", 4.0, 3.5, 2, "dog"))
a.collect({case a: Int    => "is integer" |
          case b: String => "is string" }).collect
res17: Array[String] = Array(is string, is string, is integer, is string)

val myfunc: PartialFunction[Any, Any] = {
  case a: Int    => "is integer" |
  case b: String => "is string" }
myfunc.isDefinedAt("")
res21: Boolean = true

myfunc.isDefinedAt(1)
res22: Boolean = true

myfunc.isDefinedAt(1.5)
res23: Boolean = false
```


这里需要注意，上面的代码之所有能够work，是因为partialFunction仅仅检查了item类型。如果想要对item进行操作，需要明确申明哪种数据类型可以进行计算。否则编译器不知道如何进行计算。

```scala
val myfunc2: PartialFunction[Any, Any] = {case x if (x < 4) => "x"}
<console>:10: error: value < is not a member of Any

val myfunc2: PartialFunction[Int, Any] = {case x if (x < 4) => "x"}
myfunc2: PartialFunction[Int,Any] = <function1>
```

## flatMap

跟map类似，但能够在对每个item的map操作中输出多个item

函数原型：

	def flatMap[U: ClassTag](f: T => TraversableOnce[U]): RDD[U]

例子：

``` scala

val a = sc.parallelize(1 to 10, 5)
a.flatMap(1 to _).collect
res47: Array[Int] = Array(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

sc.parallelize(List(1, 2, 3), 2).flatMap(x => List(x, x, x)).collect
res85: Array[Int] = Array(1, 1, 1, 2, 2, 2, 3, 3, 3)

// The program below generates a random number of copies (up to 10) of the items in the list.
val x  = sc.parallelize(1 to 10, 3)
x.flatMap(List.fill(scala.util.Random.nextInt(10))(_)).collect

res1: Array[Int] = Array(1, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10)
```


## flatMapValues
  
和mapValues相似，但是将map函数返回的value进行展开并和key一起返回。

函数原型：

	def flatMapValues[U](f: V => TraversableOnce[U]): RDD[(K, U)]

例子：

```scala

val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val b = a.map(x => (x.length, x))
b.flatMapValues("x" + _ + "x").collect
res6: Array[(Int, Char)] = Array((3,x), (3,d), (3,o), (3,g), (3,x), (5,x), (5,t), (5,i), (5,g), (5,e), (5,r), (5,x), (4,x), (4,l), (4,i), (4,o), (4,n), (4,x), (3,x), (3,c), (3,a), (3,t), (3,x), (7,x), (7,p), (7,a), (7,n), (7,t), (7,h), (7,e), (7,r), (7,x), (5,x), (5,e), (5,a), (5,g), (5,l), (5,e), (5,x))
```

## fold
  
对每个partition中的values进行aggregate操作，aggregation的初始值为用户提供的zeroValue。

注意：


* fold原理等同aggregate函数，只不过是简化版的aggregate，既第一轮和第二轮reduce的函数是同一个函数
* eroValue会作用在每一个partition的reduce函数上
* 同时zeroValue也会作用在对所有partition的结果进行第二次reduce的操作上

函数原型：

	def fold(zeroValue: T)(op: (T, T) => T): T

例子：

```scala
val a = sc.parallelize(List(1,2,3), 3)
a.fold(0)(_ + _)
res59: Int = 6

// 注意，这里zeroValue不仅参与了partition中的reduce计算，也参与了第二轮的reduce计算
val b = sc.parallelize(List("1","2","3"), 3)
b.fold("x")(_+_))  // 等同于 b.aggregate("x")(_+_, _+_)
res39: String = xx1x2x3
```

例子2：

```scala
val employeeData = sc.parallelize(List(("Jack",1000.0),("Bob",2000.0),("Carl",7000.0)),3)
val dummyEmployee = ("nobody", 0.0)

// 从这里可以更加直观的看出，zeroValue作为初始值参与了每个partition内部的运算
// 且每一次的op运算，都返回一个值，与下一个变量进行下一轮的op运算
val mostSalaryEmployee = employeeData.fold(dummyEmployee)( (most, employee) => if(most._2 < employee._2) employee else most )
println("employee with maximum salary is"+maxSalaryEmployee)
```


## foldByKey [Pair]
  
和fold相似，但是对RDD中的每一个key对应的values进行fold操作。该function仅仅对于RDD结构是twoD tuples的情况下使用。

函数原型：

	def foldByKey(zeroValue: V)(func: (V, V) => V): RDD[(K, V)]
	def foldByKey(zeroValue: V, numPartitions: Int)(func: (V, V) => V): RDD[(K, V)]
	def foldByKey(zeroValue: V, partitioner: Partitioner)(func: (V, V) => V): RDD[(K, V)]

例子：

```scala
val a = sc.parallelize(List("dog", "cat", "owl", "gnu", "ant"), 2)
val b = a.map(x => (x.length, x))
b.foldByKey("")(_ + _).collect
res84: Array[(Int, String)] = Array((3,dogcatowlgnuant)

val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val b = a.map(x => (x.length, x))
b.foldByKey("")(_ + _).collect
res85: Array[(Int, String)] = Array((4,lion), (3,dogcat), (7,panther), (5,tigereagle))
```

例子2：

```scala
val deptEmployeesRDD = sc.parallelize(List(
                                          ("cs",("jack",1000.0)),
                                          ("cs",("bron",1200.0)),
                                          ("phy",("sam",2200.0)),
                                          ("phy",("ronaldo",500.0))),4)
val dummy = ("nobody", 0.0)

// foldByKey的计算方式跟fold完全一样
// 唯一不同的就是op计算函数按照key的划分在每个key对应的所有values内进行计算
val maxByDept = deptEmployeesRDD.foldByKey(dummy)( (most, employee) => if(most._2 < employee._2) employee else most )
println("maximum salaries in each dept" + maxByDept.collect().toList)
```


## foreach
  
对RDD中的每一个item执行函数参数中的操作

函数原型：

	def foreach(f: T => Unit)

例子：

```scala

val c = sc.parallelize(List("cat", "dog", "tiger", "lion", "gnu", "crocodile", "ant", "whale", "dolphin", "spider"), 3)
c.foreach(x => println(x + "s are yummy"))

lions are yummy
gnus are yummy
crocodiles are yummy
ants are yummy
whales are yummy
dolphins are yummy
spiders are yummy
```

## foreachPartition

对RDD的每个partition中的item执行函数参数中的操作。对partition中items的访问方式是通过iterator参数进行。

函数原型：

	def foreachPartition(f: Iterator[T] => Unit)

例子：

```scala

val b = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9), 3)
b.foreachPartition(x => println(x.reduce(_ + _)))
6
15
24
```

## fullOuterJoin [Pair]
  
对两个paired RDD进行按key的full outer Join操作。

注意：

* 返回的RDD中，每个key对应的value都会有两个值，且都是Option值。
* 如果某个key在其中一个RDD中没有对应的value，也会用None填补。

函数原型：

	def fullOuterJoin[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (Option[V], Option[W]))]
	def fullOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (Option[V], Option[W]))]
	def fullOuterJoin[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (Option[V], Option[W]))]

例子：

```scala

val pairRDD1 = sc.parallelize(List( ("cat",2), ("cat", 5), ("book", 4),("cat", 12)))
val pairRDD2 = sc.parallelize(List( ("cat",2), ("cup", 5), ("mouse", 4),("cat", 12)))
pairRDD1.fullOuterJoin(pairRDD2).collect

res5: Array[(String, (Option[Int], Option[Int]))] = Array((book,(Some(4),None)), (mouse,(None,Some(4))), (cup,(None,Some(5))), (cat,(Some(2),Some(2))), (cat,(Some(2),Some(12))), (cat,(Some(5),Some(2))), (cat,(Some(5),Some(12))), (cat,(Some(12),Some(2))), (cat,(Some(12),Some(12))))
```

## getCheckpointFile
  
返回该RDD的checkpoint file路径，如果该RDD还没有被checkpoint过，则返回null

函数原型：

	def getCheckpointFile: Option[String]

例子：

```scala
sc.setCheckpointDir("/home/cloudera/Documents")
val a = sc.parallelize(1 to 500, 5)
val b = a++a++a++a++a
b.getCheckpointFile
res49: Option[String] = None

b.checkpoint
b.getCheckpointFile
res54: Option[String] = None

b.collect
b.getCheckpointFile
res57: Option[String] = Some(file:/home/guili.ll/Documents/cb978ffb-a346-4820-b3ba-d56580787b20/rdd-40)
```

## getStorageLevel
  
获取RDD的当前存储level。若要修改一个RDD的存储level，只有当该RDD还没有被设置过storage leve。如果已经设置过了，就不能重新设置。

函数原型：

	def getStorageLevel

例子：

```scala

val a = sc.parallelize(1 to 100000, 2)
a.persist(org.apache.spark.storage.StorageLevel.DISK_ONLY)
a.getStorageLevel.description
String = Disk Serialized 1x Replicated

a.cache
java.lang.UnsupportedOperationException: Cannot change storage level of an RDD after it was already assigned a level
```


## glom

将各个partition中的items组合成新的RDD返回

函数原型：

def glom(): RDD[Array[T]]

例子：

```scala
val a = sc.parallelize(1 to 100, 3)
a.glom.collect
res8: Array[Array[Int]] = Array(Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33), Array(34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66), Array(67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100))
```



##groupBy
  

函数原型：

	def groupBy[K: ClassTag](f: T => K): RDD[(K, Iterable[T])]
	def groupBy[K: ClassTag](f: T => K, numPartitions: Int): RDD[(K, Iterable[T])]
	def groupBy[K: ClassTag](f: T => K, p: Partitioner): RDD[(K, Iterable[T])]
	
注意:

* 传递给groupBy的操作函数的输出结果作为group的key
* 返回值是一个Array，每个item是[key, Iterator]的格式

例子：


```scala
val a = sc.parallelize(1 to 9, 3)
a.groupBy(x => { if (x % 2 == 0) "even" else "odd" }).collect
res42: Array[(String, Seq[Int])] = Array((even,ArrayBuffer(2, 4, 6, 8)), (odd,ArrayBuffer(1, 3, 5, 7, 9)))


val a = sc.parallelize(1 to 9, 3)
def myfunc(a: Int) : Int =
{
  a % 2
}
a.groupBy(myfunc).collect
res3: Array[(Int, Seq[Int])] = Array((0,ArrayBuffer(2, 4, 6, 8)), (1,ArrayBuffer(1, 3, 5, 7, 9)))

val a = sc.parallelize(1 to 9, 3)
def myfunc(a: Int) : Int =
{
  a % 2
}
a.groupBy(x => myfunc(x), 3).collect
a.groupBy(myfunc(_), 1).collect
res7: Array[(Int, Seq[Int])] = Array((0,ArrayBuffer(2, 4, 6, 8)), (1,ArrayBuffer(1, 3, 5, 7, 9)))

import org.apache.spark.Partitioner
class MyPartitioner extends Partitioner {
	def numPartitions: Int = 2
	def getPartition(key: Any): Int =
	{
   		key match
    	{
      		case null     => 0
      		case key: Int => key          % numPartitions
      		case _        => key.hashCode % numPartitions
    	}
  	}
  	override def equals(other: Any): Boolean =
  	{
    	other match
    	{
      		case h: MyPartitioner => true
      		case _                => false
    	}
  	}
}
val a = sc.parallelize(1 to 9, 3)
val p = new MyPartitioner()
val b = a.groupBy((x:Int) => { x }, p)
val c = b.mapWith(i => i)((a, b) => (b, a))
c.collect
res42: Array[(Int, (Int, Seq[Int]))] = Array((0,(4,ArrayBuffer(4))), (0,(2,ArrayBuffer(2))), (0,(6,ArrayBuffer(6))), (0,(8,ArrayBuffer(8))), (1,(9,ArrayBuffer(9))), (1,(3,ArrayBuffer(3))), (1,(1,ArrayBuffer(1))), (1,(7,ArrayBuffer(7))), (1,(5,ArrayBuffer(5))))
```


## groupByKey [Pair]
  
函数原型：

	def groupByKey(): RDD[(K, Iterable[V])]
	def groupByKey(numPartitions: Int): RDD[(K, Iterable[V])]
	def groupByKey(partitioner: Partitioner): RDD[(K, Iterable[V])]

注意:

* 返回值是一个[key, Iterator]的格式


例子：

```scala

val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "spider", "eagle"), 2)
val b = a.keyBy(_.length)
b.groupByKey.collect
res11: Array[(Int, Seq[String])] = Array((4,ArrayBuffer(lion)), (6,ArrayBuffer(spider)), (3,ArrayBuffer(dog, cat)), (5,ArrayBuffer(tiger, eagle)))
```


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


## isCheckpointed

确认是否该RDD已经被checkpointed过。

函数原型：

	def isCheckpointed: Boolean

例子：

```scala

sc.setCheckpointDir("/home/cloudera/Documents")
c.isCheckpointed
res6: Boolean = false

c.checkpoint
c.isCheckpointed
res8: Boolean = false

c.collect
c.isCheckpointed
res9: Boolean = true
```

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


## leftOuterJoin [Pair]

对两个KV RDD进行left outer Join操作。

注意：

* 两个RDD的key都必须是comparable的类型

函数原型：

	def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
	def leftOuterJoin[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (V, Option[W]))]
	def leftOuterJoin[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (V, Option[W]))]

例子：

```scala

val a = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val b = a.keyBy(_.length)
val c = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val d = c.keyBy(_.length)
b.leftOuterJoin(d).collect

res1: Array[(Int, (String, Option[String]))] = Array((6,(salmon,Some(salmon))), (6,(salmon,Some(rabbit))), (6,(salmon,Some(turkey))), (6,(salmon,Some(salmon))), (6,(salmon,Some(rabbit))), (6,(salmon,Some(turkey))), (3,(dog,Some(dog))), (3,(dog,Some(cat))), (3,(dog,Some(gnu))), (3,(dog,Some(bee))), (3,(rat,Some(dog))), (3,(rat,Some(cat))), (3,(rat,Some(gnu))), (3,(rat,Some(bee))), (8,(elephant,None)))
```

## map


函数原型：

	def map[U: ClassTag](f: T => U): RDD[U]

例子：

```scala
val a = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val b = a.map(_.length)
val c = a.zip(b)
c.collect
res0: Array[(String, Int)] = Array((dog,3), (salmon,6), (salmon,6), (rat,3), (elephant,8))
```



## mapPartitions

* 该函数仅仅在RDD的所有partition上执行一次。
* 每个partition中的items是以iterator的形式进行逐个处理
* 用户函数必须返回一个Iterator类型的集合
* 所有partition上返回的Iterator会被搜集到driver并组合成一个新的RDD


函数原型：

	def mapPartitions[U: ClassTag](f: Iterator[T] => Iterator[U], preservesPartitioning: Boolean = false): RDD[U]

Example 1:

```scala
val a = sc.parallelize(1 to 9, 3)
def myfunc[T](iter: Iterator[T]) : Iterator[(T, T)] = {
  var res = List[(T, T)]()
  var pre = iter.next
  while (iter.hasNext)
  {
    val cur = iter.next;
    res .::= (pre, cur)
    pre = cur;
  }
  res.iterator
}
a.mapPartitions(myfunc).collect
res0: Array[(Int, Int)] = Array((2,3), (1,2), (5,6), (4,5), (8,9), (7,8))
```


Example 2:

```scala
val x = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9,10), 3)
def myfunc(iter: Iterator[Int]) : Iterator[Int] = {
  var res = List[Int]()
  while (iter.hasNext) {
    val cur = iter.next;
    res = res ::: List.fill(scala.util.Random.nextInt(10))(cur)
  }
  res.iterator
}
x.mapPartitions(myfunc).collect
// some of the number are not outputted at all. This is because the random number generated for it is zero.
res8: Array[Int] = Array(1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 5, 7, 7, 7, 9, 9, 10)
```

以上示例还可以用flatMap实现：

Example 2 using flatmap

```scala
val x  = sc.parallelize(1 to 10, 3)
x.flatMap(List.fill(scala.util.Random.nextInt(10))(_)).collect

res1: Array[Int] = Array(1, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10)
```



## mapPartitionsWithIndex


和mapPartitions类似，但参数函数接受两个参数

* 第一个参数为该partition在所有partitions中的index
* 第二个参数为item的iterator。
* 参数函数的返回值必须也要是一个iterator

函数原型：

	def mapPartitionsWithIndex[U: ClassTag](f: (Int, Iterator[T]) => Iterator[U], preservesPartitioning: Boolean = false): RDD[U]


例子：

```scala

val x = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10), 3)
def myfunc(index: Int, iter: Iterator[Int]) : Iterator[String] = {
  iter.toList.map(x => index + "," + x).iterator
}
x.mapPartitionsWithIndex(myfunc).collect()
res10: Array[String] = Array(0,1, 0,2, 0,3, 1,4, 1,5, 1,6, 2,7, 2,8, 2,9, 2,10)
```



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



## max

函数原型：

	def max()(implicit ord: Ordering[T]): T

例子：

```scala

val y = sc.parallelize(10 to 30)
y.max
res75: Int = 30

val a = sc.parallelize(List((10, "dog"), (3, "tiger"), (9, "lion"), (18, "cat")))
a.max
res6: (Int, String) = (18,cat)
```


## mean [Double], meanApprox [Double]


函数原型：

	def mean(): Double
	def meanApprox(timeout: Long, confidence: Double = 0.95): PartialResult[BoundedDouble]

例子：

```scala

val a = sc.parallelize(List(9.1, 1.0, 1.2, 2.1, 1.3, 5.0, 2.0, 2.1, 7.4, 7.5, 7.6, 8.8, 10.0, 8.9, 5.5), 3)
a.mean
res0: Double = 5.3
```


## min

函数原型：

	def min()(implicit ord: Ordering[T]): T

例子：

```scala
val y = sc.parallelize(10 to 30)
y.min
res75: Int = 10


val a = sc.parallelize(List((10, "dog"), (3, "tiger"), (9, "lion"), (8, "cat")))
a.min
res4: (Int, String) = (3,tiger)
```


## partitionBy [Pair]

Repartitions as key-value RDD using its keys. The partitioner implementation can be supplied as the first argument.

函数原型：

	def partitionBy(partitioner: Partitioner): RDD[(K, V)]
	
	
## persist, cache 

这两个函数用来调整RDD的storage level。当spark从内存中free RDD的数据时，会根据RDD的storage level来决定哪些partitions需要被保持在内存，哪些应该被输出存储到持久介质中。cache()相当于persist(StorageLevel.MEMORY_ONLY)。

注意：

* 一旦storage level被设置，那么就不能在更改

函数原型：

	def cache(): RDD[T]
	def persist(): RDD[T]
	def persist(newLevel: StorageLevel): RDD[T]


例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog", "Gnu", "Rat"), 2)
c.getStorageLevel
res0: org.apache.spark.storage.StorageLevel = StorageLevel(false, false, false, false, 1)
c.cache
c.getStorageLevel
res2: org.apache.spark.storage.StorageLevel = StorageLevel(false, true, false, true, 1)
```


## pipe 

take各个partition是中的item作为输入参数从stdin传入到command string表示的shell命令中，shell命令产出的结果为RDD的输出值

注意：

* 这里command是对每个partition的`所有数据`进行一次执行，而不是对每个partition中的所有item一个一个都执行一次命令
* 每个partition中的items都以一个'\n'分开
* 空partition也会执行一次该命令

函数原型：

	def pipe(command: String): RDD[String]
	def pipe(command: String, env: Map[String, String]): RDD[String]
	def pipe(command: Seq[String], env: Map[String, String] = Map(), printPipeContext: (String => Unit) => Unit = null, printRDDElement: (T, String => Unit) => Unit = null): RDD[String]


例子：

```scala

val a = sc.parallelize(1 to 9, 3)
a.pipe("head -n 1").collect
res2: Array[String] = Array(1, 4, 7)
```


## reduce 


函数原型：

	def reduce(f: (T, T) => T): T


例子：

```scala

val a = sc.parallelize(1 to 100, 3)
a.reduce(_ + _)
res41: Int = 5050
```


## repartition

函数原型：

	def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T]


例子：

```scala

val rdd = sc.parallelize(List(1, 2, 10, 4, 5, 2, 1, 1, 1), 3)
rdd.partitions.length
res2: Int = 3
val rdd2  = rdd.repartition(5)
rdd2.partitions.length
res6: Int = 5
```


## repartitionAndSortWithinPartitions [Ordered]

按照参数提供的partitioner对RDD重新进行partition。并且，在每一个重新生成的partition中，按照各自item的key进行排序。

函数原型：

	def repartitionAndSortWithinPartitions(partitioner: Partitioner): RDD[(K, V)]


例子：

```scala

// first we will do range partitioning which is not sorted
val randRDD = sc.parallelize(List( (2,"cat"), (6, "mouse"),(7, "cup"), (3, "book"), (4, "tv"), (1, "screen"), (5, "heater")), 3)
val rPartitioner = new org.apache.spark.RangePartitioner(3, randRDD)
val partitioned = randRDD.partitionBy(rPartitioner)
def myfunc(index: Int, iter: Iterator[(Int, String)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
partitioned.mapPartitionsWithIndex(myfunc).collect

res0: Array[String] = Array([partID:0, val: (2,cat)], [partID:0, val: (3,book)], [partID:0, val: (1,screen)], [partID:1, val: (4,tv)], [partID:1, val: (5,heater)], [partID:2, val: (6,mouse)], [partID:2, val: (7,cup)])


// now lets repartition but this time have it sorted
val partitioned = randRDD.repartitionAndSortWithinPartitions(rPartitioner)
def myfunc(index: Int, iter: Iterator[(Int, String)]) : Iterator[String] = {
  iter.toList.map(x => "[partID:" +  index + ", val: " + x + "]").iterator
}
partitioned.mapPartitionsWithIndex(myfunc).collect

res1: Array[String] = Array([partID:0, val: (1,screen)], [partID:0, val: (2,cat)], [partID:0, val: (3,book)], [partID:1, val: (4,tv)], [partID:1, val: (5,heater)], [partID:2, val: (6,mouse)], [partID:2, val: (7,cup)])
```


## rightOuterJoin [Pair]

对两个KV RDD进行inner Join操作。

注意：

* 两个RDD的key都必须是comparable的类型

函数原型：

	def rightOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (Option[V], W))]
	def rightOuterJoin[W](other: RDD[(K, W)], numPartitions: Int): RDD[(K, (Option[V], W))]
	def rightOuterJoin[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (Option[V], W))]


例子：

```scala

val a = sc.parallelize(List("dog", "salmon", "salmon", "rat", "elephant"), 3)
val b = a.keyBy(_.length)
val c = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val d = c.keyBy(_.length)
b.rightOuterJoin(d).collect

res2: Array[(Int, (Option[String], String))] = Array((6,(Some(salmon),salmon)), (6,(Some(salmon),rabbit)), (6,(Some(salmon),turkey)), (6,(Some(salmon),salmon)), (6,(Some(salmon),rabbit)), (6,(Some(salmon),turkey)), (3,(Some(dog),dog)), (3,(Some(dog),cat)), (3,(Some(dog),gnu)), (3,(Some(dog),bee)), (3,(Some(rat),dog)), (3,(Some(rat),cat)), (3,(Some(rat),gnu)), (3,(Some(rat),bee)), (4,(None,wolf)), (4,(None,bear)))
```

## sortBy

该函数对input RDD进行排序，并生成新的RDD。

* 第一个参数为一个函数，对item进行计算后生成的要对按其进行排序的key
* 第二个参数（optional）为asc or desc

函数原型：

	def sortBy[K](f: (T) ⇒ K, ascending: Boolean = true, numPartitions: Int = this.partitions.size)(implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]

例子：

```scala

val y = sc.parallelize(Array(5, 7, 1, 3, 2, 1))
y.sortBy(c => c, true).collect
res101: Array[Int] = Array(1, 1, 2, 3, 5, 7)

y.sortBy(c => c, false).collect
res102: Array[Int] = Array(7, 5, 3, 2, 1, 1)

val z = sc.parallelize(Array(("H", 10), ("A", 26), ("Z", 1), ("L", 5)))
z.sortBy(c => c._1, true).collect
res109: Array[(String, Int)] = Array((A,26), (H,10), (L,5), (Z,1))

z.sortBy(c => c._2, true).collect
res108: Array[(String, Int)] = Array((Z,1), (L,5), (H,10), (A,26))
```

## sortByKey [Ordered]

This function sorts the input RDD's data and stores it in a new RDD. The output RDD is a shuffled RDD because it stores data that is output by a reducer which has been shuffled. The implementation of this function is actually very clever. First, it uses a range partitioner to partition the data in ranges within the shuffled RDD. Then it sorts these ranges individually with mapPartitions using standard sort mechanisms.

函数原型：

	def sortByKey(ascending: Boolean = true, numPartitions: Int = self.partitions.size): RDD[P]

例子：

```scala

val a = sc.parallelize(List("dog", "cat", "owl", "gnu", "ant"), 2)
val b = sc.parallelize(1 to a.count.toInt, 2)
val c = a.zip(b)
c.sortByKey(true).collect
res74: Array[(String, Int)] = Array((ant,5), (cat,2), (dog,1), (gnu,4), (owl,3))
c.sortByKey(false).collect
res75: Array[(String, Int)] = Array((owl,3), (gnu,4), (dog,1), (cat,2), (ant,5))

val a = sc.parallelize(1 to 100, 5)
val b = a.cartesian(a)
val c = sc.parallelize(b.takeSample(true, 5, 13), 2)
val d = c.sortByKey(false)
res56: Array[(Int, Int)] = Array((96,9), (84,76), (59,59), (53,65), (52,4))
```
