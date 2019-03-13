package luoli523.backtobasic;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ArrayListTest {

  // Constructor accepting Collection
  @Test
  public void createTest() {
    // Stream创建Collections的一些用法
    // 这里返回的numbers是一个HashSet
    Collection<Integer> numbers = IntStream.range(0, 10).boxed().collect(Collectors.toSet());
    List<Integer> list = new ArrayList<>(numbers);
    assertEquals(10, list.size());
    assertTrue(numbers.containsAll(list));
  }

  public void addElementTest() {
    // add elements
    List<Long> list = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
    LongStream.range(4,10)
            .boxed()
            .collect(Collectors.toList());
  }

  @Test
  public void iteratingListTest() {
    List<Integer> list = new ArrayList(
            IntStream.range(0, 10).boxed().collect(Collectors.toList())
    );

    // listIterator返回的迭代器可以指定到任何一个位置
    ListIterator<Integer> it = list.listIterator(list.size());
    List<Integer> result = new ArrayList<>(list.size());
    while(it.hasPrevious()) {
      result.add(it.previous());
    }

    // Collections.reverse是in place的reverse
    // reverse后list变成了[9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
    Collections.reverse(list);
    assertEquals(result, list);
  }

  @Test
  // search array list
  public void searchTest() {
    List<String> list = LongStream.range(0, 16)
            .boxed()
            .map(Long::toHexString)
            .collect(Collectors.toCollection(ArrayList::new));
    List<String> stringsToSearch = new ArrayList<>(list);
    stringsToSearch.addAll(list);
    System.out.println(stringsToSearch);
    assertEquals(10, stringsToSearch.indexOf("a"));
    assertEquals(26, stringsToSearch.lastIndexOf("a"));

    Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));
    List<String> result = stringsToSearch
            .stream()
            .filter(matchingStrings::contains)
            .collect(Collectors.toCollection(ArrayList::new));
    assertEquals(6, result.size());
  }
}
