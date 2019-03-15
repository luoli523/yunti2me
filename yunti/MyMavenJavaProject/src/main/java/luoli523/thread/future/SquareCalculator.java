package luoli523.thread.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SquareCalculator {

  //private ExecutorService service = Executors.newSingleThreadExecutor();
  private ExecutorService service = Executors.newFixedThreadPool(2);

  public Future<Integer> calculate(Integer input) {
    return service.submit(() -> {
      Thread.sleep(1000);
      return input * input;
    });
  }

  public void shutdown() {
    service.shutdown();
    try {
      service.awaitTermination(1000, TimeUnit.MILLISECONDS);
      service.shutdownNow();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
