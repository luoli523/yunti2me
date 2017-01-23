## first
  
返回该RDD中的第一个元素

`注：这是一个action操作，会触发实际计算`

函数原型：

  def first(): T

例子：

```scala

val c = sc.parallelize(List("Gnu", "Cat", "Rat", "Dog"), 2)
c.first
res1: String = Gnu
```

