## getCheckpointFile
  
返回该RDD的checkpoint file路径，如果该RDD还没有被checkpoint过，则返回null

函数原型：

  def getCheckpointFile: Option[String]

例子：

```scala
sc.setCheckpointDir("/home/cloudera/Documents")
val a = sc.parallelize(1 to 500, 5)
val b = a++a++a++a++a
b.getCheckpointFile
res49: Option[String] = None

b.checkpoint
b.getCheckpointFile
res54: Option[String] = None

b.collect
b.getCheckpointFile
res57: Option[String] = Some(file:/home/guili.ll/Documents/cb978ffb-a346-4820-b3ba-d56580787b20/rdd-40)
```


