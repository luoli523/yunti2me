# 查看各RDD items在partitions上的分布(mapPartitionsWithIndex)

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
```