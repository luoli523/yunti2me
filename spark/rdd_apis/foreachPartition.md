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

