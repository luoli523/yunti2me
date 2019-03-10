package luoli523.java8.lambda;

import org.jruby.RubyProcess;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
  }

}
