package luoli523.java8.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


class Something {
  String startWith(String s) {
    return String.valueOf(s.charAt(0));
  }
}

// How does lambda expressions fit into Javas type system?
// Each lambda corresponds to a given type, specified by an interface.
// A so called functional interface must contain exactly one abstract method declaration.
// Each lambda expression of that type will be matched to this abstract method.
// Since default methods are not abstract you're free to add default methods to your functional interface.

// We can use arbitrary interfaces as lambda expressions as long as the interface only contains one abstract method.
// To ensure that your interface meet the requirements, you should add the @FunctionalInterface annotation.
// The compiler is aware of this annotation and throws a compiler error as soon as you try to
// add a second abstract method declaration to the interface.
@FunctionalInterface
interface Converter<F, T> {
  T convert(F from);
  default void haha() {} // 该接口的default method不是abstract方法，所以lambda语法对应到的是function interface中唯一的abstract方法，既convert()
}

public class LambdaTest {

  public static void main(String[] args) {
    List<String> list = Arrays.asList("luoli","lijin","tangtang","guagua");

    Collections.sort(list, new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    });
    System.out.println(list);

    Collections.shuffle(list);

    //Collections.sort(list, (String a, String b) -> a.compareTo(b));
    Collections.sort(list, (a, b) -> a.compareTo(b));
    System.out.println(list);

    Converter<String, Integer> converter = (from -> Integer.valueOf(from));
    // Java 8 enables you to pass references of methods or constructors via the :: keyword.
    Converter<String, Integer> converter2 = Integer::valueOf;
    Integer converted = converter.convert("123");
    Integer converted2 = converter2.convert("123");
    System.out.println(converted);
    System.out.println(converted2);

    //  we can also reference object methods to function interface
    Converter<String, String> startWithConverter = new Something()::startWith;
    System.out.println(startWithConverter.convert("Java"));
  }

}
