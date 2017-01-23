##groupBy
  

函数原型：

  def groupBy[K: ClassTag](f: T => K): RDD[(K, Iterable[T])]
  def groupBy[K: ClassTag](f: T => K, numPartitions: Int): RDD[(K, Iterable[T])]
  def groupBy[K: ClassTag](f: T => K, p: Partitioner): RDD[(K, Iterable[T])]
  
注意:

* 传递给groupBy的操作函数的输出结果座位group的key
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


