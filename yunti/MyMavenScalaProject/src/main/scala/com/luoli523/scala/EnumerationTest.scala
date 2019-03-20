package com.luoli523.scala

object DAY extends Enumeration {
  type DAY = Value
  val MON, TUE, WED, THE, FRI, SAT, SUN = Value
}

object EnumerationTest extends App {

  import DAY._

  val currentDay = FRI
  println(s"today is $currentDay")

  DAY.values.foreach(println)
}
