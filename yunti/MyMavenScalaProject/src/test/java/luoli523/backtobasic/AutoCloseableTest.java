package luoli523.backtobasic;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

public class AutoCloseableTest {

  @Test
  public void whenTryWResourcesExits_thenResourceClosed() throws IOException {
    // 这里用了一个try-with-resources机制
    // try-with-resources是java7中简洁的书写try-catch-final的语法
    try(CloseableResource resource = new CloseableResource()) {
      String line = resource.readFirstLine();
      assertEquals(line, "Apache Kylin");
    }
  }

  @Test
  public void orderOfClosingResources() throws Exception {
    try(AutoCloseableResourcesFirst first = new AutoCloseableResourcesFirst();
        AutoCloseableResourcesSecond second = new AutoCloseableResourcesSecond()) {
      first.doSomething();
      second.doSomething();
    }
  }

  class CloseableResource implements AutoCloseable {
    private BufferedReader reader;

    public CloseableResource() {
      InputStream input = this.getClass()
              .getClassLoader()
              .getResourceAsStream("textfile.txt");
      reader = new BufferedReader(new InputStreamReader(input));
    }

    public String readFirstLine() throws IOException {
      String firstLine = reader.readLine();
      return firstLine;
    }

    @Override
    public void close() {
      try {
        reader.close();
        System.out.println("Closed BufferedReader in the close method");
      } catch (IOException e) {
        //handle exception
      }
    }
  }

  class AutoCloseableResourcesFirst implements AutoCloseable {
    @Override
    public void close() throws Exception {
      System.out.println("Closed AutoCloseableResources_First");
    }

    public AutoCloseableResourcesFirst() {
      System.out.println("Constructor -> AutoCloseableResources_First");
    }

    public void doSomething() {
      System.out.println("doSomething -> AutoCloseableResources_First");
    }
  }

  class AutoCloseableResourcesSecond implements AutoCloseable {
    @Override
    public void close() throws Exception {
      System.out.println("Closed AutoCloseableResources_Second");
    }

    public AutoCloseableResourcesSecond() {
      System.out.println("Constructor -> AutoCloseableResources_Second");
    }

    public void doSomething() {
      System.out.println("doSomething -> AutoCloseableResources_Second");
    }
  }
}

