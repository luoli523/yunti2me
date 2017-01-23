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


