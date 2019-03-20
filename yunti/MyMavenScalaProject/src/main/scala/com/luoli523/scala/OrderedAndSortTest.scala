package com.luoli523.scala

object OrderedAndSortTest extends App{

  class Person(val name:String, val age:Int) extends Ordered[Person] {
    override def compare(that: Person): Int = {
      this.name.compareTo(that.name)
    }

    override def toString: String = s"$name:$age"
  }
  
  object Person {
    def apply(name: String, age: Int): Person = new Person(name, age)
  }

  val dd = Array( Person("luoli",36),Person("haha",12), Person("niubi",23), Person("huirong",25) )
  println(dd.mkString(" "))
  println(dd.sorted.mkString(" "))


  import scala.util.Sorting._

  println(dd.mkString(" "))
  quickSort(dd)
  println(dd.mkString(" "))

}
