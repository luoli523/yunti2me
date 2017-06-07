# 在scala中使用break和continue

Scala中默认是没有实现break和continue关键字的。但是提供了其他的解决方案来达到同样的效果，那就是`scala.util.control.Breaks`。

如下示例如何通过它达到相同的代码流程控制：

```scala

import util.control.Breaks._

  println("\n=== BREAK EXAMPLE ===")
  breakable {
    for (i <- 1 to 10) {
      println(i)
      if (i > 4) 
        break // break out of the for loop
    } 
  }
  
  var numPs = 0
    breakable {
      if (searchMe.charAt(i) != 'p') {
        break // break out of the 'breakable', continue the outside loop 
      } else {
        numPs += 1 
      }
    } 
  }
}
```

运行结果如下：

```
=== BREAK EXAMPLE ===
5
```