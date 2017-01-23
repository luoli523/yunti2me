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


