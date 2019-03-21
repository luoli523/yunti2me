package luoli523.thread.sender_receiver;

import java.util.concurrent.ThreadLocalRandom;

public class Sender implements Runnable {
  private Data data;

  public Sender(Data data) {
    this.data = data;
  }

  @Override
  public void run() {
    String packets[] = {
            "First packet",
            "Second packet",
            "Third packet",
            "Fourth packet",
            "End"
    };

    for (String packet : packets) {
      data.send(packet);

      try {
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.out.println("Thread interrupted");
      }
    }
  }
}
