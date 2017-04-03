# 在scala中使用break和continue

Scala中默认是没有实现break和continue关键字的。但是提供了其他的解决方案来达到同样的效果，那就是`scala.util.control.Breaks`。

如下示例如何通过它达到相同的代码流程控制：

```scala

import util.control.Breaks._
object BreakAndContinueDemo extends App {
  println("\n=== BREAK EXAMPLE ===")
  breakable {
    for (i <- 1 to 10) {
      println(i)
      if (i > 4) 
        break // break out of the for loop
    } 
  }
    println("\n=== CONTINUE EXAMPLE ===")  val searchMe = "peter piper picked a peck of pickled peppers"
  var numPs = 0  for (i <- 0 until searchMe.length) {
    breakable {
      if (searchMe.charAt(i) != 'p') {
        break // break out of the 'breakable', continue the outside loop 
      } else {
        numPs += 1 
      }
    } 
  }  println("Found " + numPs + " p's in the string.")
}
```

运行结果如下：

```
=== BREAK EXAMPLE ===1234
5=== CONTINUE EXAMPLE ===Found 9 p's in the string.
```