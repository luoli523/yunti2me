package luoli523.ref;

import java.lang.ref.*;
import java.util.WeakHashMap;

public class SoftRefDemo {

  public static void main(String[] args) throws Exception {

    softRefDemo();
    simpleUseWeakRefDemo();
    weakHashMapDemp();
    refQueueDemo();
    simpleUsePhantomRefDemo();
  }

  private static void softRefDemo() {
    SoftReference<String> sr = new SoftReference<String>(new String("hello java ref"));
    System.out.println(sr.get());
  }

  private static void simpleUseWeakRefDemo() {
    WeakReference<String> sr = new WeakReference<String>(new String("hello weak ref"));
    //WeakReference<String> sr = new WeakReference<String>("hello weak ref");
    // before gc -> hello weak ref
    System.out.println(sr.get());
    // gc
    System.gc();
    // after g -> null
    System.out.println(sr.get());
  }

  private static void weakHashMapDemp() {
    WeakHashMap<String, String> weakHashMap = new WeakHashMap<String, String>();
    String key1 = new String("key1");
    String key2 = new String("key2");
    String key3 = new String("key3");
    weakHashMap.put(key1, "value1");
    weakHashMap.put(key2, "value2");
    weakHashMap.put(key3, "value3");
    // 使没有任何强引用指向key1
    key1 = null;
    System.out.println("before gc weakHashMap = " + weakHashMap + " , size=" + weakHashMap.size());
    // 通知JVM的gc进行垃圾回收
    System.gc();
    System.out.println("after gc weakHashMap = " + weakHashMap + " , size="+ weakHashMap.size());
  }

  /**
   * 引用队列demo
   */
  private static void refQueueDemo() {
    final ReferenceQueue<String> refQueue = new ReferenceQueue<String>();
    // 用于检查引用队列中的引用值被回收
    Thread checkRefQueueThread = new Thread() {

      public void run() {
        while (true) {
          Reference<? extends String> clearRef = refQueue.poll();
          if (null != clearRef) {
            System.out.println("引用对象被回收, ref = " + clearRef + ", value = " + clearRef.get());
          }
        }
      }
    };
    checkRefQueueThread.start();
    WeakReference<String> weakRef1 = new WeakReference<String>(new String("value1"), refQueue);
    WeakReference<String> weakRef2 = new WeakReference<String>(new String("value2"), refQueue);
    WeakReference<String> weakRef3 = new WeakReference<String>(new String("value3"), refQueue);

    System.out.println("ref1 value = " + weakRef1.get() + ", ref2 value = " + weakRef2.get()
            + ", ref3 value = " + weakRef3.get());
    System.out.println("开始通知JVM的gc进行垃圾回收");
    // 通知JVM的gc进行垃圾回收
    System.gc();
  }

  /**
   * 简单使用虚引用demo
   * 虚引用在实现一个对象被回收之前必须做清理操作是很有用的,比finalize()方法更灵活
   */
  private static void simpleUsePhantomRefDemo() throws InterruptedException {
    Object obj = new Object();
    ReferenceQueue<Object> refQueue = new ReferenceQueue<Object>();
    PhantomReference<Object> phantomRef = new PhantomReference<Object>(obj, refQueue);
    // null
    System.out.println(phantomRef.get());
    // null
    System.out.println(refQueue.poll());
    obj = null;
    // 通知JVM的gc进行垃圾回收
    System.gc();
    // null, 调用phantomRef.get()不管在什么情况下会一直返回null
    System.out.println(phantomRef.get());
    // 当GC发现了虚引用，GC会将phantomRef插入进我们之前创建时传入的refQueue队列
    // 注意，此时phantomRef对象，并没有被GC回收，在我们显式地调用refQueue.poll返回phantomRef之后
    // 当GC第二次发现虚引用，而此时JVM将phantomRef插入到refQueue会插入失败，此时GC才会对phantomRef对象进行回收
    Thread.sleep(200);
    Reference<?> pollObj = refQueue.poll();
    // java.lang.ref.PhantomReference@1540e19d
    System.out.println(pollObj);
    if (null != pollObj) {
      // 进行资源回收的操作
    }
  }
}
