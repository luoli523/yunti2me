package luoli523.thread;

import java.util.concurrent.ThreadLocalRandom;

public class MyThread implements Runnable {

  private int num = 0;
  MyThread(int n) {this.num = n;}

  public void run() {
    try {
      Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Thread " + num);
  }

  public static void main(String[] args) {
    for(int i = 0; i < 10; i++) {
      new Thread(new MyThread(i)).start();
    }
  }
}
