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


