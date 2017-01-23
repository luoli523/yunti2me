# 模式匹配和PartialFunction

一个case序列加上一对大括号可以在任何地方呗当做函数使用。实际上，这种情况本身就是一个函数。不同于普通函数只有一个统一的入口，和参数列表，case序列的这种函数有多个入口，每一个入口拥有不同的参数列表。如下例：

```scala
val withDefault: Option[Int] => Int = {
    case Some(x) => x
    case None => 0
}
```

上述函数的函数体还有两个case，第一个case匹配一个Some，并返回Some内部的数字；第二个case匹配一个None，并返回0。 上述函数的调用情况如下：

```
  scala> withDefault(Some(10))
  res25: Int = 10
  
  scala> withDefault(None)
  res26: Int = 0
```

实际上，case序列的函数是Scala中的`PartialFunction`，如果咋运行时给予一个该PartialFunction不支持的参数，那么就会收到`runtime-exception`。 如下例，就是一个返回List中第二个元素的PartialFunction：

```scala
val second: List[Int] => Int = {
  case x :: y :: _ => y
}
```

可以看出，该匹配列表并没有匹配全部的可能性，因此该代码在编译的时候会收到相关的警告： 

```
<console>:10: warning: match may not be exhaustive.
It would fail on the following inputs: List(_), Nil
       val second : List[Int] => Int = {
                                       ^
second: List[Int] => Int = <function1>
```

所以该函数只有在调用参数是一个包含三个元素的List的时候才能成功，如果调用参数是空List，则会失败：

```
  scala> second(List(5,6,7))
  res24: Int = 6
  
  scala> second(List())
  scala.MatchError: List()
        at $anonfun$1.apply(<console>:17)
        at $anonfun$1.apply(<console>:17)
```

这种情况下，如果需要检查一个partial function对于某些参数是否提供了匹配，需要首先`告诉`编译器，让其`知道`你提供的是一个partial function。 上述函数在定义时， `List[Int] => Int` 包括了所有的接受List[Int]参数，并返回Int的函数定义，而不管该函数是否是PartialFunction。如果想要`告诉`编译器该函数的确就是一个PartialFunction，则可以将函数定义如下：

```scala
val second : PatitialFunction[List[Int], Int] = {
   case x :: y :: _ => y
}
```

这样second就完全是一个PartialFunction的定义，而所有的PartialFunction都有一个`isDefinedAt`函数，用来确认对于其参数类型，是否定义了相应的匹配case。对于以上定义，由于之定义了包含三个元素的List的定义，那么对于其他类型的参数,`isDefinedAt`就会返回`false`。运行如下例：

```
  scala> second.isDefinedAt(List(5,6,7))
  res27: Boolean = true
  
  scala> second.isDefinedAt(List())
  res28: Boolean = false
```

上述PartialFunction的定义，等同于如下定义：

```scala
  new PartialFunction[List[Int], Int] {
    def apply(xs: List[Int]) = xs match {
      case x :: y :: _ => y 
    }
    def isDefinedAt(xs: List[Int]) = xs match {
      case x :: y :: _ => true
      case _ => false
    }
  }
```