package main

import "fmt"
import "math"
import "math/rand"
import "runtime"
import "time"

func pow(x, n, lim float64) float64 {
  if v := math.Pow(x, n); v < lim {
    return v
  } else {
    fmt.Printf("%g >= %g\n", v, lim)
  }
  return lim
}

func Sqrt(x float64) float64 {
  z := rand.Float64()
  for ; z - math.Sqrt(x) > 0.001 || z - math.Sqrt(x) < -0.001; {
    z = z - (z*z - x)/2*z
  }
    return z
}

func main() {
  sum := 0
  for i :=0; i < 10; i++ {
    sum += i
  }
  fmt.Println(sum)

  sum = 1
  for ; sum < 1000; {
    sum += sum
  }
  fmt.Println(sum)

  fmt.Println(
    pow(3,2,4),
    pow(3,2,20),
  )

  fmt.Println(Sqrt(2))

  switch os := runtime.GOOS; os {
  case "darwin":
    fmt.Println("OS X")
  case "linux":
    fmt.Println("Linux")
  default: 
    fmt.Printf("%s", os)
  }

  today := time.Now().Weekday()
  switch time.Saturday {
  case today + 0:
    fmt.Println("Today")
  case today + 1:
    fmt.Println("Tomorrow")
  case today + 2:
    fmt.Println("In two days")
  default:
    fmt.Println("Too far away")
  }

  t := time.Now()
  switch {
  case t.Hour() < 12:
    fmt.Println("Good morning!")
  case t.Hour() < 17:
    fmt.Println("Good afternoon.")
  default:
    fmt.Println("Good evening.")
  }

  defer fmt.Println("Hello")
  fmt.Println("World")

  fmt.Println("counting")
  for i := 0; i < 10; i++ {
    defer fmt.Println(i)
  }
  fmt.Println("done")
}