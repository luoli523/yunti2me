package luoli523.classloader;

public class ClassLoaderTest {

  /*
   The application class loader loads the class where the example method is contained.
   An application or system class loader loads our own files in the classpath.
   eg: JAVA_OME/rt.jar

   Next, the extension one loads the Logging class.
   Extension class loaders load classes that are an extension of the standard core Java classes.
   eg: JAVA_OME/lib/ext/*.jar

   Finally, the bootstrap one loads the ArrayList class.
   A bootstrap or primordial class loader is the parent of all the others.
   eg: your/path/to/app.jar in the -classpath

   However, we can see that the last out, for the ArrayList it displays null in the output.
   This is because the bootstrap class loader is written in native code,
   not Java – so it doesn’t show up as a Java class. Due to this reason,
   the behavior of the bootstrap class loader will differ across JVMs.
   */
  // 1, bootstrap classloader
  // 2, extension classloader
  // 3, application classloader
  public static void main(String[] args) {
    System.out.println("class loader for HashMap: " + java.util.HashMap.class.getClassLoader());
    System.out.println("class loader for DNSNameService: " + sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());
    System.out.println("class loader for this class: " + ClassLoaderTest.class.getClassLoader());
    System.out.println(com.mysql.jdbc.Blob.class.getClassLoader());
  }

}