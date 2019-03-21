package luoli523.equals;

class Geek {
  private int id;
  private String name;

  public Geek(String name, int id) {
    this.id = id;
    this.name = name;
  }

  @Override
  public int hashCode() {
    // We are returning the Geek_id
    // as a hashcode value.
    // we can also return some
    // other calculated value or may
    // be memory address of the
    // Object on which it is invoked.
    // it depends on how you implement
    // hashCode() method.
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    // check if both the object references are
    // refering to the same object.
    if(this == obj)
      return true;

    // checks if the argument is of the
    // type Geek by comparing the classes
    // of the passed argument and this object.
    // if(!(obj instanceOf Geek)) return false;
    if(obj == null || this.getClass() != obj.getClass())
      return false;

    // type casting the argument
    Geek geek = (Geek) obj;

    // comparing the state of argument with
    // the state of 'this' Object.
    return (geek.name == this.name && geek.id == this.id);
  }
}

public class GeekEqualsCode {

  public static void main (String[] args) {
    // creating the Objects of Geek class.
    Geek g1 = new Geek("aa", 1);
    Geek g2 = new Geek("aa", 1);

    // comparing above created Objects.
    if(g1.hashCode() == g2.hashCode()) {
      if(g1.equals(g2))
        System.out.println("Both Objects are equal. ");
      else
        System.out.println("Both Objects are not equal. ");
    }
    else
      System.out.println("Both Objects are not equal. ");
  }
}
