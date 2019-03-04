package luoli523.guava;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ImmutableListEx {

  public static void main(String[] args) {
    unmodifiableCollectionExample();
    immutableListOfExample();
  }

  /**
     Collections.unmodifiableList() 返回的只是这个List的一个不可变视图，
     如果原List中有元素被修改，其视图中的元素也会同样被影响到
   */
  private static void unmodifiableCollectionExample() {
    System.out.println("=====================================");
    List list = Arrays.asList("luoli", "genius", "tiancai");
    System.out.println("List to be modified: " + list);
    List unmodifiableList = Collections.unmodifiableList(list);
    System.out.println("modify the wrapped list");
    list.set(0, "LUOLI");
    System.out.println("After the change:" + unmodifiableList);
    System.out.println("=====================================");
  }

  // ImmutableList.of 用例
  private static void immutableListOfExample() {
    System.out.println("=====================================");

    List<String> emptyList = ImmutableList.of();
    verifyElements("ImmutableList.of()", emptyList);

    List<String> singletonList = ImmutableList.of("I am Single");
    verifyElements("ImmutableList.of(\"I am Single\")", singletonList);

    List<String> dualElementList = ImmutableList.of("Me", "and you");
    verifyElements("ImmutableList.of(\"Me\", \"and you\")", dualElementList);

    List<String> multipleElements = ImmutableList.of("Once", "upon",
            "time", "there", "used", "to", "be", "king");
    verifyElements("multipleElementsList", multipleElements);

    List<Integer> moreThanElevenElements = ImmutableList.of(1, 2, 3, 4, 5,
            6, 7, 8, 9, 10, 11, 12);
    verifyElements("moreThanElevenElements", moreThanElevenElements);

    System.out.println("=====================================");
  }

  private static <T> void verifyElements(String name, List<T> list) {
    System.out.print("List Name: " + name);
    if(list.isEmpty()) {
      System.out.println(" is empty");
    } else {
      System.out.print((list.size() == 1 ? " has one element: " : " has "
              + list.size() + " elements: "));
      if(list.size() == 1) {
        System.out.println(list.get(0));
      } else {
        list.forEach(new Consumer<T>() {
          public void accept(T t) {
            System.out.print(t + " ");
          }
        });
        System.out.println();
      }
    }
  }

}
