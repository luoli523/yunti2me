package luoli523.backtobasic;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class SynchronizedTest {

  @Test
  public void givenMultiThread_whenNonSyncMethod() throws InterruptedException {
    LuoliSynchronizedMethods cal = new LuoliSynchronizedMethods();
    ExecutorService service = Executors.newFixedThreadPool(3);
    IntStream.range(0,1000).forEach( i -> service.submit(cal::calculate));
    service.awaitTermination(1000, TimeUnit.MILLISECONDS);
    assertNotEquals(1000, cal.getSum());
  }

  @Test
  public void givenMultiThread_whenSyncMethod() throws InterruptedException {
    LuoliSynchronizedMethods cal = new LuoliSynchronizedMethods();
    ExecutorService service = Executors.newFixedThreadPool(3);
    IntStream.range(0,1000).forEach( i -> service.submit(cal::synchronizedCalculate));
    service.awaitTermination(1000, TimeUnit.MILLISECONDS);
    assertEquals(1000, cal.getSum());
  }

  class LuoliSynchronizedMethods {
    private int sum = 0;

    public int getSum() {
      return sum;
    }

    public void setSum(int sum) {
      this.sum = sum;
    }

    public void calculate() {
      setSum(getSum()+1);
    }

    public synchronized void synchronizedCalculate() {
      setSum(getSum()+1);
    }
  }
}
