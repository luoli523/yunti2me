
# finally中获取对象引用

在scala的try/catch/finally中，通常的处理情况如下：

```scala
try { 
  openAndReadAFile(filename)
  // case _: Throwable => println("exception ignored")
  case e: Throwable => t.printStackTrace()
```

但是有一种情况特殊，就是当需要finally中，利用一些对象的引用来进行操作（比如关闭文件，或者socket等引用时），需要在finally中拿到这些对象的引用。那么利用上述方式就无法达到。比如：

```scala
try { 
  val in = new FileInputStream("/tmp/Test.class")
  val out = new FileOutputStream("/tmp/Test.class.copy")
  // case _: Throwable => println("exception ignored")
  case e: Throwable => t.printStackTrace()
  // compile error
  in.close()
  out.close()
}
```

这种情况下，`finally`中无法获取两个文件的引用，也就无法进行`close`操作。

可以通过如下方式来解决：

```scala
import java.io._

  var out = None:Option[FileOutputStream]
      out.get.write(c)
  } catch {
  } finally {
    if (in.isDefined) in.get.close 
    if (out.isDefined) out.get.close
  } 
}```