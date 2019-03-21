package luoli523.multithread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class ExecutorServiceTest {
  private ExecutorService service;

  // Runable中run()方法不接受任何参数，也没有返回值
  Runnable runnableTask;
  // Callable的call()方法接受返回值，用Future来hold
  Callable<String> callableTask;
  List<Callable<String>> callables = new ArrayList<>();

  @Before
  public void startUp() {
    service = Executors.newFixedThreadPool(10);
    runnableTask = () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };
    callableTask = () -> {
      TimeUnit.MILLISECONDS.sleep(300);
      return "callableTask execution";
    };
    callables.add(callableTask);
    callables.add(callableTask);
    callables.add(callableTask);
  }

  // The execute() method is void, and it doesn’t give any possibility to get the result of
  // task’s execution or to check the task’s status (is it running or executed).
  @Test
  public void executeTest() {
    // execute()的参数必须是Runnable，不能是Callable
    service.execute(runnableTask);
  }

  // submit() submits a Callable or a Runnable task to an ExecutorService and
  // returns a result of type Future.
  @Test
  public void submitTest() throws Exception {
    // submit()的参数可以是Runnable，也可以是Callable，或者Callable Collection
    service.submit(runnableTask);

    Future<String> future = service.submit(callableTask);

    assertEquals(future.get(), "callableTask execution");
  }

  // invokeAny() assigns a collection of tasks to an ExecutorService, causing each to be executed,
  // and returns the result of a successful execution of one task (if there was a successful execution).
  @Test
  public void invokeAnyTest() throws Exception {
    String result = service.invokeAny(callables);
    assertEquals(result, "callableTask execution");
  }

  @Test
  public void invokeAllTest() throws Exception {
    List<Future<String>> futures = service.invokeAll(callables);
    assertEquals(futures.size(), 3);
    futures.stream().forEach(f -> {
      try {
        // future.get()操作会阻塞调用线程，知道能够拿到调用返回值为止，所以最好使用get(time)
        assertEquals(f.get(100, TimeUnit.MILLISECONDS), "callableTask execution");
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Test
  public void scheduledExecutorServiceTest() {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    // To schedule a single task’s execution after a fixed delay,
    // us the scheduled() method of the ScheduledExecutorService.
    // There are two scheduled() methods that allow you to execute Runnable or Callable tasks:
    // The code below delays for one second before executing callableTask.
    scheduledExecutorService.schedule(callableTask, 1, TimeUnit.SECONDS);

    // The following block of code will execute a task after an initial delay of 100 milliseconds,
    // and after that, it will execute the same task every 450 milliseconds.
    // If the processor needs more time to execute an assigned task than the period parameter of the
    // scheduleAtFixedRate() method, the ScheduledExecutorService will wait until the current
    // task is completed before starting the next
    scheduledExecutorService.scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);

    // If it is necessary to have a fixed length delay between iterations of the task,
    // scheduleWithFixedDelay() should be used. For example,
    // the following code will guarantee a 150-millisecond pause between the end of the
    // current execution and the start of another one
    scheduledExecutorService.scheduleWithFixedDelay(runnableTask, 100, 150, TimeUnit.MILLISECONDS);
  }

  // One good way to shut down the ExecutorService (which is also recommended by Oracle)
  // is to use both of these methods: shutdown() & shutdownNow().
  // combined with the awaitTermination() method. With this approach,
  // the ExecutorService will first stop taking new tasks,
  // the wait up to a specified period of time for all tasks to be completed.
  // If that time expires, the execution is stopped immediately:
  @After
  public void shutDown() {
    service.shutdown();
    try {
      service.awaitTermination(800, TimeUnit.MILLISECONDS);
      service.shutdownNow();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
