## combineByKey[Pair] 

Very efficient implementation that combines the values of a RDD consisting of two-component tuples by applying multiple aggregators one after another.


函数原型：

  def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C): RDD[(K, C)]
  def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numPartitions: Int): RDD[(K, C)]
  def combineByKey[C](createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, partitioner: Partitioner, mapSideCombine: Boolean = true, serializerClass: String = null): RDD[(K, C)]
  
例子：

```scala

val a = sc.parallelize(List("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"), 3)
val b = sc.parallelize(List(1,1,2,2,2,1,2,2,2), 3)
val c = b.zip(a)

// 第一个函数对每一个key，用来将key的第一个value初始化到一个特定的结构
// 第二个函数用来在每一个partition中进行对初始化结构和values中的每一个item进行reduce函数运算
// 第三个函数用来在对每一个partition上产生的结果按照相同的key再次按照第三个reduce函数进行计算
val d = c.combineByKey(List(_), (x:List[String], y:String) => y :: x, (x:List[String], y:List[String]) => x ::: y)

d.collect
res16: Array[(Int, List[String])] = Array((1,List(cat, dog, turkey)), (2,List(gnu, rabbit, salmon, bee, bear, wolf)))
```


