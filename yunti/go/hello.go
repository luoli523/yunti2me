package main

import (
  "fmt"
  "time"
  "math"
  "math/rand"
  "math/cmplx"
)

func add1(x int, y int) int {
    return x + y
}

func add2(x, y int) int {
  return x + y
}

func split(sum int) (x, y int) {
  x = sum * 4 / 9
  y = sum - x
  return
}

var (
  ToBe bool = false
  MaxInt uint64 = 1 << 64 - 1
  z complex128 = cmplx.Sqrt(-5 + 12i)
)

func main() {
  fmt.Println("Hello, Go!")

  fmt.Println("The time is ", time.Now())

  fmt.Println(math.Pi)
  fmt.Println("My Favorite number is ", rand.Intn(10))

  fmt.Println("2 + 3 = ", add1(2,3))
  fmt.Println("42 + 31 = ", add2(42,31))
  fmt.Println(split(17))

  //var c, python, java = true, false, "no!"
  c, python, java := true, false, "no!"
  var i, j int = 1, 2
  k := 3
  fmt.Println(i, j, k, c, python, java)


  const f = "%T(%v)\n"
  fmt.Printf(f, ToBe, ToBe)
  fmt.Printf(f, MaxInt, MaxInt)
  fmt.Printf(f, z, z)

  x,y := 3, 4
  var f2 float64 = math.Sqrt(float64(x*x + y*y))
  var z uint = uint(f2)
  fmt.Println(x,y,z)
}