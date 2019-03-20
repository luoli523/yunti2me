package com.luoli523.scala

object FoldLeftTest {

  def main(args: Array[String]): Unit = {

    val ops = List(reverse, toUpper, appendBar)

    println(applyTransformations1("foo", ops))
    println(applyTransformations2("foo", ops))
    println(applyTransformations3("foo", ops))
  }

  def reverse = (str : String) => str.reverse
  def toUpper = (str : String) => str.toUpperCase
  def appendBar = (str : String) => str + "Bar"

  def applyTransformations1(initial : String, ops : Seq[(String => String)]) : String = {
    ops.foldLeft(initial) {
      (cur, op) => op(cur)
    }
  }

  def applyTransformations2(initial : String, ops : Seq[(String => String)]) : String = {
    var cur = initial
    for (op <- ops) {
      cur = op(cur)
    }
    cur
  }

  def applyTransformations3(initial : String, ops : Seq[(String => String)]) : String = {
    ops match {
      case head::tail => applyTransformations3(head(initial), tail)
      case Nil => initial
    }
  }
}
