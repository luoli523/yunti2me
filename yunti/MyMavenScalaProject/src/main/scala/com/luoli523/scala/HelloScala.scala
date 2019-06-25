package com.luoli523.scala

object HelloScala {
  def main(args : Array[String]): Unit = {
    val list_a = List(5, 21, 61, 25, 11, 59)
    val list_b = List(17, 81, 0, 7)
    val list_c = List(42, 8, 25, 33, 2)
    print(foo(list_a, list_b, list_c))
  }

  def foo(list_a : List[Int], list_b : List[Int], list_c : List[Int]) : List[Any] = {
    var x = List[Int]()
    var y = 1000

    for (a <- list_a) {
      for (b <- list_b) {
        for (c <- list_c) {
          var z = 0
          if (Math.abs(a-b) < Math.abs(a-c))
            z = Math.abs(a-c)
          else
            z = Math.abs(a-b)

          if (z < Math.abs(b-c))
            z = Math.abs(b-c)

          if (z < y) {
            y = z
            x = a :: b :: c :: Nil
          }
        }
      }

    }
    return x
  }
}
