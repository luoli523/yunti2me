package luoli523.java8.lambda;

import org.jruby.RubyProcess;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {

  public static void main(String[] args) {
    List<String> list = Arrays.asList("abc", "aaa", "bbb", "ccc", "aaa2", "fff", "bbb2", "ccc3" , "aaa3");

    // Stream filter && sorted && forEach && map
    list.stream()
            .filter((s) -> s.startsWith("a"))
            .forEach(System.out::println);

    list.stream()
            .sorted((a, b) -> b.compareTo(a))
            .filter((a) -> a.startsWith("a"))
            .forEach(System.out::println);

    list.stream()
            .map((a) -> a.toUpperCase())
            .sorted((a, b) -> b.compareTo(a))
            .filter((a) -> a.length() > 3)
            .forEach(System.out::println);

    // Stream match
    boolean anyStartWithA = list.stream().anyMatch((a) -> a.startsWith("a"));
    System.out.println(anyStartWithA);

    boolean allStartWithA = list.stream().allMatch((a) -> a.startsWith("a"));
    System.out.println(allStartWithA);

    boolean noneStartWithZ = list.stream().noneMatch((a) -> a.startsWith("z"));
    System.out.println(noneStartWithZ);

    // Stream count
    long startWithAAndLengthThanThree = list.stream().filter(a -> a.startsWith("a")).filter(a -> a.length() > 3).count();
    System.out.println(startWithAAndLengthThanThree);

    // Stream reduce
    Optional<String> reduced = list.stream().sorted().reduce((a, b) -> a + "#" + b);
    reduced.ifPresent(System.out::println);



    // Parallel Stream
    int max = 100000;
    List<String> uuidList = new ArrayList<String>(max);
    for(int i = 0; i < max; i++) {
      UUID uuid = UUID.randomUUID();
      uuidList.add(uuid.toString());
    }

    long t0 = System.nanoTime();
    long count = uuidList.stream().sorted().count();
    System.out.println(count);
    long t1 = System.nanoTime();
    long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
    System.out.println(String.format("sequential sort took: %d ms", millis));

    t0 = System.nanoTime();
    count = uuidList.parallelStream().sorted().count();
    System.out.println(count);
    t1 = System.nanoTime();
    millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
    System.out.println(String.format("parallel sort took: %d ms", millis));


    // Map using stream like api
    Map<Integer, String> map = new HashMap<>();

    for (int i = 0; i < 10; i++) {
      map.putIfAbsent(i, "val" + i);
    }

    map.forEach((k,v) -> System.out.println("key:" + k + " || value:" + v));

    map.computeIfPresent(3, (k, v) -> v + k);
    System.out.println(map.get(3)); // val33

    map.computeIfPresent(9, (k, v) -> null);
    System.out.println(map.containsKey(9)); // false

    map.computeIfAbsent(23, k -> "val" + k);
    System.out.println(map.containsKey(23)); // false
    System.out.println(map.get(23)); // val23

    map.computeIfAbsent(3, k -> "three");
    System.out.println(map.get(3)); // val33

    // remove entries for a a given key, only if it's currently mapped to a given value
    map.remove(3, "val_noExists");
    System.out.println(map.get(3)); // val33
    map.remove(3, "val33");
    System.out.println(map.containsKey(3)); // false

    map.getOrDefault(42, "not found");  // not found

    map.merge(9, "val9", (value, newValue) -> value + newValue);
    System.out.println(map.get(9)); // val9
    map.merge(9, "concat", (value, newValue) -> value + newValue);
    System.out.println(map.get(9)); // val9concat


    // Create Stream
    Stream.of("one", "two", "three")
            .sorted()
            .forEach(System.out::print);
    System.out.println();
    IntStream.range(1,5)
            .filter(a -> a % 2 != 0)
            .forEach(System.out::print);
    System.out.println();

    Stream.of("a1", "a2", "a3")
            .map(s -> s.substring(1))
            .mapToInt(Integer::valueOf)
            .mapToObj(a -> "a" + a)
            .forEach(System.out::println);
    IntStream.range(1,5)
            .map(n -> n * 2 + 1)
            .average()
            .ifPresent(System.out::println);

    // Stream execute order
    // 每个element执行的顺序是一个element走通所有的函数，然后下一个
    Stream.of("d1", "a2", "c4", "b2", "c")
            .filter(s -> {
              System.out.println("filter: " + s);
              return true;
            })
            .forEach(s -> {
              System.out.println("forEach: " + s);
            });

    // d1 D1 a2 A2, 执行到a2后就停止了
    Stream.of("d1", "a2", "c4", "b2", "c")
            .map(s -> {
              System.out.println("map: " + s);
              return s.toUpperCase();
            })
            .anyMatch(s -> {
              System.out.println("map: " + s);
              return s.startsWith("A");
            });

    // stream的函数执行顺序很重要，合理的布局能够降低很大的执行代价
    // 第二种执行顺序就比第一种少运行了3次map
    System.out.println("==================");
    Stream.of("d2", "a2", "b1", "b3", "c")
            .map(s -> {
              System.out.println("map: " + s);
              return s.toUpperCase();
            })
            .filter(s -> {
              System.out.println("filter: " + s);
              return s.startsWith("A");
            })
            .forEach(s -> System.out.println("forEach: " + s));
    Stream.of("d2", "a2", "b1", "b3", "c")
            .filter(s -> {
              System.out.println("filter: " + s);
              return s.startsWith("a");
            })
            .map(s -> {
              System.out.println("map: " + s);
              return s.toUpperCase();
            })
            .forEach(s -> System.out.println("forEach: " + s));


    // sorted执行顺序是排序后再往下传，既水平执行顺序，因为sort操作是包含状态的操作（stateful operator）
    Stream.of(1,9,2,8,3,7)
            .sorted((a,b) -> {
              System.out.println("sorted: " + a + "&" + b);
              return a.compareTo(b);
            })
            .anyMatch( a -> a % 2 == 0);

    // stream对象是不可复用的，每次调用了函数后的stream就不能再次使用
    // 如果需要频繁构建stream，可以使用supplier
    Supplier<Stream<String>> supplier = () ->
      Stream.of("b1","c2","a3","c").filter(s -> s.startsWith("a"));
    supplier.get().anyMatch(s -> true);
    supplier.get().noneMatch(s->true);


    // collect
    List<Person> persons = Arrays.asList(
            new Person("luoli",37),
            new Person("lijin",35),
            new Person("tang",5),
            new Person("gua",1)
    );

    List<Person> filtered = persons.stream()
            .filter(p -> p.name.startsWith("l"))
            .collect(Collectors.toList());

    filtered.forEach(System.out::println);

    Map<Integer, List<Person>> personsByAge = persons.stream()
            .collect(Collectors.groupingBy(p -> p.age));
    personsByAge.forEach((age, p) -> System.out.format("age : %d \t p : %s\n", age, p));
  }

}

class Person {
  String name;
  int age;

  public Person(String name, int age) {
    this.name = name;
    this.age = age;
  }

  @Override
  public String toString() {
    return name;
  }
}
