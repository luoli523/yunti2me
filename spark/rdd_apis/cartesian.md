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
