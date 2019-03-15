package luoli523.thread.sender_receiver;

import java.util.concurrent.ThreadLocalRandom;

public class Receiver implements Runnable {
  private Data data;

  public Receiver(Data data) {
    this.data = data;
  }

  @Override
  public void run() {
    for (String receivedMessage = data.receive();
            !"End".equals(receivedMessage);
            receivedMessage = data.receive()) {
      System.out.println(receivedMessage);

      try {
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.out.println("Thread interrupted");
      }
    }
  }
}
