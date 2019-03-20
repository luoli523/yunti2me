package com.luoli523.scala

class Pizza(var size:Int, var ptype:String) {

  def this(crustSize:Int) {
    this(crustSize, Pizza.DEFAULT_CRUST_TYPE)
  }

  def this(ptype : String) {
    this(Pizza.DEFAULT_CRUST_SIZE, ptype)
  }

  def this() {
    this(Pizza.DEFAULT_CRUST_SIZE, Pizza.DEFAULT_CRUST_TYPE)
  }
}

object Pizza {
  val DEFAULT_CRUST_SIZE = 12
  val DEFAULT_CRUST_TYPE = "THIN"
}

object PizzaMain extends App {
  val p1 = new Pizza(Pizza.DEFAULT_CRUST_SIZE, Pizza.DEFAULT_CRUST_TYPE)
  val p2 = new Pizza(Pizza.DEFAULT_CRUST_SIZE)
  val p3 = new Pizza(Pizza.DEFAULT_CRUST_TYPE)
  val p4 = new Pizza
}