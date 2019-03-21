package luoli523.backtobasic;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MakeArrayListImmutableTest {

  // 使用原生JDK Collections.unmodifiableList(list);
  @Test(expected = UnsupportedOperationException.class)
  public void givenUsingTheJdk_whenUnmodifiableListIsCreated_thenNotModifiable() {
    // Arrays.asList返回的是一个fixed-size list，本身就无法list进行增删，所有这里需要new一个新list
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    List<String> unmodifiableList = Collections.unmodifiableList(list);
    unmodifiableList.add("four");
  }

  // 使用Guava ImmutableList
  @Test(expected = UnsupportedOperationException.class)
  public void givenUsingGuava_whenUnmodifiableListIsCreated_thenNotModifiable() {
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    ImmutableList<String> ummutableList = ImmutableList.copyOf(list);
    ummutableList.add("four");
  }

  // 使用Apache Collections Commons
  @Test(expected = UnsupportedOperationException.class)
  public void givenUsingCommonsCollections_whenUnmodifiableListIsCreated_thenNotModifiable() {
    List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
    List<String> ummutableList = ListUtils.unmodifiableList(list);
    ummutableList.add("four");
  }
}
