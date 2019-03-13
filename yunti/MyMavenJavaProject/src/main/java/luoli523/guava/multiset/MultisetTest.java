package luoli523.guava.multiset;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

class MyClass{}

public class MultisetTest {

  public static void main(String[] args) {
    Multiset<MyClass> myMultiset = HashMultiset.create();

    MyClass myObject = new MyClass();

    myMultiset.add(myObject, 5); // Add 5 copies of myObject
    System.out.println(myMultiset.count(myObject)); // 5

    myMultiset.remove(myObject, 2); // remove 2 copies
    System.out.println(myMultiset.count(myObject)); // 3
  }
}
