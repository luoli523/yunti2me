## checkpoint



checkpoint函数用来将RDD中的数据进行临时持久化存储，RDD的数据会被以二进制文件的形式存储到checkpoint dir中。

注意：由于spark的lazy evaluation机制，实际的checkpoint存储动作会直到RDD action操作真正发生的时候才会执行。

另：`my_directory_name` 默认是在slave的local，该目录需要在各个slave上都存在。或者用户可以将该目录指定为HDFS目录也可以。

函数原型

    def checkpoint()

例子：

```scala
sc.setCheckpointDir("my_directory_name")

val a = sc.parallelize(1 to 4)

a.checkpoint

// action发生的时候，才会真正执行checkpoint操作
a.count
14/02/25 18:13:53 INFO SparkContext: Starting job: count at <console>:15
...
14/02/25 18:13:53 INFO MemoryStore: Block broadcast_5 stored as values to memory (estimated size 115.7 KB, free 296.3 MB)
14/02/25 18:13:53 INFO RDDCheckpointData: Done checkpointing RDD 11 to file:/home/guili.ll/Documents/spark-0.9.0-incubating-bin-cdh4/bin/my_directory_name/65407913-fdc6-4ec1-82c9-48a1656b95d6/rdd-11, new parent is RDD 12
res23: Long = 4
```


