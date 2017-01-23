## getStorageLevel
  
获取RDD的当前存储level。若要修改一个RDD的存储level，只有当该RDD还没有被设置过storage leve。如果已经设置过了，就不能重新设置。

函数原型：

  def getStorageLevel

例子：

```scala

val a = sc.parallelize(1 to 100000, 2)
a.persist(org.apache.spark.storage.StorageLevel.DISK_ONLY)
a.getStorageLevel.description
String = Disk Serialized 1x Replicated

a.cache
java.lang.UnsupportedOperationException: Cannot change storage level of an RDD after it was already assigned a level
```


