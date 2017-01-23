# Spark Best Practice

## 使用partial Fucntion处理bad input

拿filter来举例：

```scala
val a = sc.parallelize(1 to 10, 3)
val b = a.filter(_ % 2 == 0)
b.collect
res3: Array[Int] = Array(2, 4, 6, 8, 10)
```

如上代码，由于 `(_ % 2)== 0` 函数的执行对于所有的RDD item都没有问题，所有这个程序不会产生问题，也不需要对脏数据进行处理。但如果是如下程序：

```scala
val a = sc.parallelize(List("cat", "horse", 4.0, 3.5, 2, "dog"))
a.filter(_ < 4).collect
<console>:15: error: value < is not a member of Any
```

由于List中不仅包含了Int，还包含了Double和String类型的数据，因此对List进行`_<4`的函数执行就会出现异常。如果List就是要处理的大数据量数据，那么其中可能会有一些这样的所谓`脏数据`需要进行类似`skip`的特殊处理，才能够让程序顺利的执行完，并且对正确的数据做正确的处理，并忽略掉脏数据。


实际上在collect调用函数的时候，是通过调用scala的partial function的isDefinedAt属性来判断该数据是否适合进行该数据。只有当item数据在该判断上返回true，才会进行函数参数的运算。

比如如下代码：

```scala
val a = sc.parallelize(List("cat", "horse", 4.0, 3.5, 2, "dog"))
a.collect({case a: Int    => "is integer" |
           case b: String => "is string" }).collect
res17: Array[String] = Array(is string, is string, is integer, is string)

val myfunc: PartialFunction[Any, Any] = {
  case a: Int    => "is integer" |
  case b: String => "is string" }
myfunc.isDefinedAt("")
res21: Boolean = true

myfunc.isDefinedAt(1)
res22: Boolean = true

myfunc.isDefinedAt(1.5)
res23: Boolean = false
```

上述代码中，case子句几位partial function，针对Int类型和String类型进行匹配，仅仅搜集这两种类型的item，其他类型则直接跳过。

另外，以上代码之所以能work，是因为只检查了item类型既进行搜集，如果要对item进行其他计算，需要partial function声明item类型，如下例：

```scala
val myfunc2: PartialFunction[Any, Any] = {case x if (x < 4) => "x"}
<console>:10: error: value < is not a member of Any

val myfunc2: PartialFunction[Int, Any] = {case x if (x < 4) => "x"}
myfunc2: PartialFunction[Int,Any] = <function1>


val list = sc.parallelize(List("dog","cat", 1,3,2.5,"rabbit"),3)
list: org.apache.spark.rdd.RDD[Any] = ParallelCollectionRDD[39] at parallelize at <console>:27
list.filter{ case x : Int => x < 4; case s:String => s.length < 4; case _ => false}.collect
res55: Array[Any] = Array(dog, cat, 1, 3)
```
