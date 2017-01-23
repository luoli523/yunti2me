## fold
  
对每个partition中的values进行aggregate操作，aggregation的初始值为用户提供的zeroValue。

注意：


* fold原理等同aggregate函数，只不过是简化版的aggregate，既第一轮和第二轮reduce的函数是同一个函数
* eroValue会作用在每一个partition的reduce函数上
* 同时zeroValue也会作用在对所有partition的结果进行第二次reduce的操作上

函数原型：

  def fold(zeroValue: T)(op: (T, T) => T): T

例子：

```scala
val a = sc.parallelize(List(1,2,3), 3)
a.fold(0)(_ + _)
res59: Int = 6

// 注意，这里zeroValue不仅参与了partition中的reduce计算，也参与了第二轮的reduce计算
val b = sc.parallelize(List("1","2","3"), 3)
b.fold("x")(_+_))  // 等同于 b.aggregate("x")(_+_, _+_)
res39: String = xx1x2x3
```

例子2：

```scala
val employeeData = sc.parallelize(List(("Jack",1000.0),("Bob",2000.0),("Carl",7000.0)),3)
val dummyEmployee = ("nobody", 0.0)

// 从这里可以更加直观的看出，zeroValue作为初始值参与了每个partition内部的运算
// 且每一次的op运算，都返回一个值，与下一个变量进行下一轮的op运算
val mostSalaryEmployee = employeeData.fold(dummyEmployee)( (most, employee) => if(most._2 < employee._2) employee else most )
println("employee with maximum salary is"+maxSalaryEmployee)
```



## foldByKey [Pair]
  
和fold相似，但是对RDD中的每一个key对应的values进行fold操作。该function仅仅对于RDD结构是twoD tuples的情况下使用。

函数原型：

  def foldByKey(zeroValue: V)(func: (V, V) => V): RDD[(K, V)]
  def foldByKey(zeroValue: V, numPartitions: Int)(func: (V, V) => V): RDD[(K, V)]
  def foldByKey(zeroValue: V, partitioner: Partitioner)(func: (V, V) => V): RDD[(K, V)]

例子：

```scala
val a = sc.parallelize(List("dog", "cat", "owl", "gnu", "ant"), 2)
val b = a.map(x => (x.length, x))
b.foldByKey("")(_ + _).collect
res84: Array[(Int, String)] = Array((3,dogcatowlgnuant)

val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", "eagle"), 2)
val b = a.map(x => (x.length, x))
b.foldByKey("")(_ + _).collect
res85: Array[(Int, String)] = Array((4,lion), (3,dogcat), (7,panther), (5,tigereagle))
```

例子2：

```scala
val deptEmployeesRDD = sc.parallelize(List(
                                          ("cs",("jack",1000.0)),
                                          ("cs",("bron",1200.0)),
                                          ("phy",("sam",2200.0)),
                                          ("phy",("ronaldo",500.0))),4)
val dummy = ("nobody", 0.0)

// foldByKey的计算方式跟fold完全一样
// 唯一不同的就是op计算函数按照key的划分在每个key对应的所有values内进行计算
val maxByDept = deptEmployeesRDD.foldByKey(dummy)( (most, employee) => if(most._2 < employee._2) employee else most )
println("maximum salaries in each dept" + maxByDept.collect().toList)
```


## reduce

reduce函数跟fold函数非常类似，唯一不同在于，reduce没有提供zerovalue，op操作直接将collection中的item进行两两操作，得到最后结果。

函数原型：

  def reduce(f: (T, T) => T): T

例子：

```scala

val a = sc.parallelize(1 to 100, 3)
a.reduce(_ + _)
res41: Int = 5050

// 比如fold中employee的例子，也可以不用dummy哨兵，直接使用reduce函数来求得。
val employeeData = sc.parallelize(List(("Jack",1000.0),("Bob",2000.0),("Carl",7000.0)),3)

val mostSalaryEmployee = employeeData.reduce( (one, two) => if(one._2 < two._2) two else one )
mostSalaryEmployee: (String, Double) = (Carl,7000.0)

println("employee with maximum salary is" + maxSalaryEmployee)
```

## reduceByKey

reduceByKey与foldByKey的区别同reduce与fold的区别一样，所以，以上foldByKey中的employee的例子，也可以用reduceByKey来实现：

```scala
val deptEmployeesRDD = sc.parallelize(List(
                                          ("cs",("jack",1000.0)),
                                          ("cs",("bron",1200.0)),
                                          ("phy",("sam",2200.0)),
                                          ("phy",("ronaldo",500.0))),4)


val maxByDept = deptEmployeesRDD.reduceByKey( (one, two) => if(one._2 < two._2) two else one )
println("maximum salaries in each dept" + maxByDept.collect().toList)
```