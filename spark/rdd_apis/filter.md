## filter
  
对每个RDD item执行一次参数函数，若返回true则collect到该item到新的RDD中。

函数原型：

  def filter(f: T => Boolean): RDD[T]

例子：

```scala

val a = sc.parallelize(1 to 10, 3)
val b = a.filter(_ % 2 == 0)
b.collect
res3: Array[Int] = Array(2, 4, 6, 8, 10)
```
当用户提供filter函数时，该filter函数必须能够处理所有包含在该RDD种的items。scala提供了partial functions来处理混合类型的数据。（partial function对于处理含有脏数据的结构非常有用，能够对脏数据进行特定处理的同时，对好的数据进行正常的map函数处理。）

混合数据类型without partial function的例子：

```scala
val b = sc.parallelize(1 to 8)
b.filter(_ < 4).collect
res15: Array[Int] = Array(1, 2, 3)

val a = sc.parallelize(List("cat", "horse", 4.0, 3.5, 2, "dog"))
a.filter(_ < 4).collect
<console>:15: error: value < is not a member of Any
```


这里失败的原因是因为在有一些item无法进行跟integer的比较操作。容器使用函数对象的isDefinedAt属性来判断该item是否适用于进行该函数的执行，只有返回true的item才会被用来进行map函数的计算。

混合数据类型partial function的例子：

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


这里需要注意，上面的代码之所有能够work，是因为partialFunction仅仅检查了item类型。如果想要对item进行操作，需要明确申明哪种数据类型可以进行计算。否则编译器不知道如何进行计算。

```scala
val myfunc2: PartialFunction[Any, Any] = {case x if (x < 4) => "x"}
<console>:10: error: value < is not a member of Any

val myfunc2: PartialFunction[Int, Any] = {case x if (x < 4) => "x"}
myfunc2: PartialFunction[Int,Any] = <function1>
```

