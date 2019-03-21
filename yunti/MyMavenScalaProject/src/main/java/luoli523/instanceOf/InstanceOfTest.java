package luoli523.instanceOf;

class Parent {}
class Child extends Parent {}

public class InstanceOfTest {
  public static void main(String[] args) {
    Child cobj = new Child();

    // A simple case
    if (cobj instanceof Child)
      System.out.println("cobj is instance of Child");
    else
      System.out.println("cobj is NOT instance of Child");

    // instanceof returns true for Parent class also
    if (cobj instanceof Parent)
      System.out.println("cobj is instance of Parent");
    else
      System.out.println("cobj is NOT instance of Parent");

    // instanceof returns true for all ancestors (Note : Object
    // is ancestor of all classes in Java)
    if (cobj instanceof Object)
      System.out.println("cobj is instance of Object");
    else
      System.out.println("cobj is NOT instance of Object");

    // instanceof returns false for null
    InstanceOfTest tobj = null;
    // A simple case
    if (tobj instanceof InstanceOfTest)
      System.out.println("tobj is instance of Test");
    else
      System.out.println("tobj is NOT instance of Test");

    // A parent object is not an instance of Child
    Parent pobj = new Parent();
    if (pobj instanceof Child)
      System.out.println("pobj is instance of Child");
    else
      System.out.println("pobj is NOT instance of Child");


    // A parent reference referring to a Child is an instance of Child
    // Reference is Parent type but object is
    // of child type.
    Parent pcobj = new Child();
    if (pcobj instanceof Child)
      System.out.println("pcobj is instance of Child");
    else
      System.out.println("pcobj is NOT instance of Child");
  }
}
