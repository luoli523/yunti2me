# Match表达式的各种匹配模式

match表达式有多种case匹配模式，比如有常量匹配模式，变量匹配模式，构造函数匹配模式，sequence匹配模式，tuple匹配模式，类型匹配模式等等。

```scala

def echoWhatYouGaveMe(x: Any): String = x match {   // constant patterns
   case 0 => "zero"
   case true => "true"
   case "hello" => "you said 'hello'" case Nil => "an empty List"   
   // sequence patterns
   case List(0, _, _) => "a three-element list with 0 as the first element"
   case List(1, _*) => "a list beginning with 1, having any number of elements" 
   case Vector(1, _*) => "a vector starting with 1, having any number of elements"   // tuples
   case (a, b) => s"got $a and $b"
   case (a, b, c) => s"got $a, $b, and $c"      
   // constructor patterns
   case Person(first, "Alexander") => s"found an Alexander, first name = $first" 
   case Dog("Suka") => "found a dog named Suka"      
   // typed patterns
   case s: String => s"you gave me this string: $s"
   case i: Int => s"thanks for the int: $i"
   case f: Float => s"thanks for the float: $f"
   case a: Array[Int] => s"an array of int: ${a.mkString(",")}"
   case as: Array[String] => s"an array of strings: ${a.mkString(",")}"
   case d: Dog => s"dog: ${d.name}"
   case list: List[_] => s"thanks for the List: $list" 
   case m: Map[_, _] => m.toString   
   // the default wildcard pattern
   case _ => "Unknown" 
}

// case class pattern match

trait Animalcase class Dog(name: String) extends Animal 
case class Cat(name: String) extends Animal 
case object Woodpecker extends Animal

object CaseClassTest extends App {  def determineType(x: Animal): String = x match {    case Dog(moniker) => "Got a Dog, name = " + moniker 
    case _:Cat => "Got a Cat (ignoring the name)"
    case Woodpecker => "That was a Woodpecker"  // matches a object
    case _ => "That was something else"  }
  println(determineType(new Dog("Rocky"))) 
  println(determineType(new Cat("Rusty the Cat")))
  println(determineType(Woodpecker))}
```

## Adding variables to patterns
如果想要在case匹配中绑定`值`到指定变量名，可以使用`@`符号。比如以上例子中：

```scala
case List(1, _*) => "a list beginning with 1, having any number of elements"
```
这个能够匹配第一个成员为`1`的List，但在`=>`右边是无法引用整个list的。

又如：

```scala
case list: List[_] => s"thanks for the List: $list"
```

这样是可以在右边引用list，但又无法匹配第一个成员为`1`这种情况。

如果写成下面这个样子呢？

```scala
case list: List(1, _*) => s"thanks for the List: $list"
```

很不幸，这种方式会产生编译错误如下：

```
Test2.scala:22: error: '=>' expected but '(' found.   case list: List(1, _*) => s"thanks for the List: $list"                       ^one error found
```

这种情况，就可以采用`@`符号对左边的变量进行变量名绑定，如下：

```scala
case list @ List(1, _*) => s"thanks for the List: $list"
```

看一个完整例子：

```scala
case class Person(firstName: String, lastName: String) 

object Test2 extends App {  def matchType(x: Any): String = x match {    //case x: List(1, _*) => s"$x" // doesn't compile
    case x @ List(1, _*) => s"$x" // works; prints the list    //case Some(_) => "got a Some" // works, but can't access the Some 
    //case Some(x) => s"$x" // works, returns "foo"
    case x @ Some(_) => s"$x" // works, returns "Some(foo)"
    case p @ Person(first, "Doe") => s"$p" 
  
    // works, returns "Person(John,Doe)" 
  }
    println(matchType(List(1,2,3))) // prints "List(1, 2, 3)"
  println(matchType(Some("foo"))) // prints "Some(foo)" 
  println(matchType(Person("John", "Doe"))) // prints "Person(John,Doe)"}```
