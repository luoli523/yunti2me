# Seq的匹配

Seq的匹配请见以下代码：

```scala
val nonEmptySeq    = Seq(1, 2, 3, 4, 5)                              // <1>
val emptySeq       = Seq.empty[Int]
val nonEmptyList   = List(1, 2, 3, 4, 5)                             // <2>
val emptyList      = Nil
val nonEmptyVector = Vector(1, 2, 3, 4, 5)                           // <3>
val emptyVector    = Vector.empty[Int]
val nonEmptyMap    = Map("one" -> 1, "two" -> 2, "three" -> 3)       // <4>
val emptyMap       = Map.empty[String,Int]

def seqToString[T](seq: Seq[T]): String = seq match {                // <5>
  case head +: tail => s"$head +: " + seqToString(tail)              // <6>
  case Nil => "Nil"                                                  // <7>
}

for (seq <- Seq(                                                     // <8>
    nonEmptySeq, emptySeq, nonEmptyList, emptyList, 
    nonEmptyVector, emptyVector, nonEmptyMap.toSeq, emptyMap.toSeq)) {
  println(seqToString(seq))
}
```

由于Seq的定义就是由一个head和一个tail构成，因此，对Seq的匹配分为两种情况，：

* 匹配至少包含一个head和一个tail的Seq（可能是List或Vector），并且tail可能是Nil，但head不是，这样至少表示该Seq是一个非空序列（head不为Nil）
* Nil，即该Seq是一个空序列

以上<5>和<6>就示例了如何匹配Seq（List || Vector）的方法。

这里很明显的暗示了一点：所有的Seq，要么是空（Nil），要么非空。

这里需要注意一下：`seqToString`函数中match的head和tail是任意的两个变量名，而不是Seq的方法名（Seq有名为head和tail的方法名），是用于提取Seq的head和tail元素的。



以下是执行结果：

```
1 +: 2 +: 3 +: 4 +: 5 +: Nil
Nil
1 +: 2 +: 3 +: 4 +: 5 +: Nil
Nil
1 +: 2 +: 3 +: 4 +: 5 +: Nil
Nil
(one,1) +: (two,2) +: (three,3) +: Nil
Nil
```

在Scala 2.10之前，处理List还有另一种很相似的方法：

```scala
val nonEmptyList = List(1, 2, 3, 4, 5)
val emptyList    = Nil

def listToString[T](list: List[T]): String = list match {
  case head :: tail => s"($head :: ${listToString(tail)})"           // <1>
  case Nil => "(Nil)"
}

for (l <- List(nonEmptyList, emptyList)) { println(listToString(l)) }
```

* 这里用 `::` 代替了 `+:`

输出也很类似：

```
(1 :: (2 :: (3 :: (4 :: (5 :: (Nil))))))
(Nil)
```