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


