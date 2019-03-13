package luoli523.classloader;

public class Foo {

  static public void main(String args[]) throws Exception {
    System.out.println("Foo Constructor >>> " + args[0] + " " + args[1]);
    Bar bar = new Bar(args[0], args[1]);
    bar.printCL();
  }

  public static void printCL() {
    System.out.println("Foo ClassLoader: " + Foo.class.getClassLoader());
  }

}

