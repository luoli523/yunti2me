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

