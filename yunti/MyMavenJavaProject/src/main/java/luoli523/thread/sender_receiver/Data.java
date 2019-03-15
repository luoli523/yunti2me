package luoli523.thread.sender_receiver;

public class Data {
  private String packet;

  // True if receiver should wait
  // False if sender should wait
  private boolean transfer = true;

  // 1, wait()的调用需要在对象锁内部（要不是synchronized方法里，要么在synchronized block里）
  // 2, wait()调用后会释放对象锁
  // 3, wait()放在while(true)内也是安全的，因为一旦wait，就释放了对象锁，并不会阻塞其他线程调用该对象的synchronized方法
  public synchronized void send(String packet) {
    while(!transfer) {
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    transfer = false;

    this.packet = packet;
    notifyAll();
  }

  public synchronized String receive() {
    while (transfer) {
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    transfer = true;

    notifyAll();
    return packet;
  }
}
