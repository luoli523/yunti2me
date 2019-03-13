package luoli523.backtobasic;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArraysAsListTest {

  // Arrays.asList(T.. t)返回的list是一个fixed-size list，不能对其进行增加和删除
  @Test(expected = UnsupportedOperationException.class)
  public void testArraysAsListFixedSize() {
    String[] arr = new String[] {"one", "two", "three"};
    List<String> list = Arrays.asList(arr);
    list.add("four");
  }

  // Arrays.asList(T.. t)返回的list是源数组的一个view，对list中elements的修改将会"写透"到源数组
  @Test
  public void testArraysAsListView() {
    String[] arr = new String[] {"one", "two", "three"};
    List<String> list = Arrays.asList(arr);
    assertEquals(arr[0], "one");
    list.set(0,"yi");
    assertEquals(arr[0], "yi");
  }

  // Arrays.asList(T.. t)返回的list，new一个新的list
  @Test
  public void testArraysAsListNewList() {
    String[] arr = new String[] {"one", "two", "three"};
    List<String> list = new ArrayList<>(Arrays.asList(arr));
    assertEquals(arr.length, 3);
    assertEquals(list.size(), 3);
    list.add("four");
    assertEquals(arr.length, 3);
    assertEquals(list.size(), 4);
  }
}
