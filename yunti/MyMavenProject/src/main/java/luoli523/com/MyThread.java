package luoli523.com;

public class MyThread implements Runnable {
  private int num = 0;
  MyThread(int n) {this.num = n;}
  public void run() {
    System.out.println("Thread " + num);
  }

  public static void main(String[] args) {
    for(int i = 0; i < 10; i++) {
      new Thread(new MyThread(i)).start();
    }
  }
}
