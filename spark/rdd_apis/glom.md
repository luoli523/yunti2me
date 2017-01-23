## glom

将各个partition中的items组合成新的RDD返回

函数原型：

def glom(): RDD[Array[T]]

例子：

```scala
val a = sc.parallelize(1 to 100, 3)
a.glom.collect
res8: Array[Array[Int]] = Array(Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33), Array(34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66), Array(67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100))
```


glom将每个partition中的items组合成一个Array，并将这些Array重新组合成一个新的RDD：

例如以下场景：

```scala
// 要找到RDD中最大的item，如果使用reduce，代码如下
val dataList = List(50.0,40.0,40.0,70.0)   
val dataRDD = sc.makeRDD(dataList)  
val maxValue =  dataRDD.reduce(_ max _)

```

以上代码虽然执行正确，但是如果dataset很大，会在各个partition间产生比较多的shuffle，性能并不好。要更高效的解决问题，可以按照如下逻辑：

* 首先找到各个partition中最大的item
* 比较各个partition中最大的item，获得整个dataset中最大的item


可以使用glom达到以上目的，代码如下：

```scala
val maxValue = dataRDD.glom().map((value:Array[Double]) => if(!value.isEmpty) value.max else Integer.MIN_VALUE).reduce(_ max _)
```

