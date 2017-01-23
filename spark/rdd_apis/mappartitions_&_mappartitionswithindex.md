# mapPartitions & mapPartitionsWithIndex

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
* 第二个参数为每个partition中items的iterator。
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

### 使用mapPartitionsWithIndex去除RDD中的第一行数据

测试的时候，常常会有这种情况：测试数据是一个表，以文本的形式保存，第一行为表的collum name，从第二行开始为每一行数据的具体values，如下表所示：

```
name age
luoli 34
guaishushu 50
weisuonan 38
```

那么测试对数据进行处理之前，常常需要将第一行去掉。使用mapPartitionsWithIndex可以方便的对已有RDD进行这项操作。

```scala
val inputData = sc.parallelize(Array("Skip this line XXXXX","Start here instead AAAA","Second line to work with BBB"))

def skipLines(index: Int, lines: Iterator[String], num: Int): Iterator[String] = {
    if (index == 0) {
      lines.drop(num)
    }
    lines
  }

val valuesRDD = inputData.mapPartitionsWithIndex((idx, iter) => skipLines(idx, iter, numLinesToSkip))

valuesRDD.collect.foreach(println)
```