package luoli523.java8;

import org.junit.Test;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class StreamReduceTest {

  // 第一种reduce：只有一个accumulator函数
  @Test
  public void reduce1() {
    OptionalInt reduced = IntStream.range(1, 4).reduce((a, b) -> a + b);
    assertEquals(reduced.getAsInt(), 6); // 1 + 2  + 3 = 6
  }

  // 第二种reduce：有一个初始值和一个accumulator函数
  @Test
  public void reduce2() {
    int reducedTwoParams = IntStream.range(1, 4).reduce(10, (a, b) -> a + b);
    assertEquals(reducedTwoParams, 16); // 10 + 1 + 2 + 3
  }

  // 第三种reduce：有一个初始值和一个accumulator函数,还有一个combinar函数
  // 这种方式只有在使用parallelStream的时候才起作用
  @Test
  public void reduce3() {
    int reducedParallel = Arrays.asList(1, 2, 3).parallelStream().reduce(10,
            (a, b) -> a + b,
            (a, b) -> {
              System.out.println("combiner was called");
              return a + b;
            }
    ); // ((10 + 1) + (10 + 2) + (10 + 3)) = 36
    assertEquals(reducedParallel, 36);
  }
}
