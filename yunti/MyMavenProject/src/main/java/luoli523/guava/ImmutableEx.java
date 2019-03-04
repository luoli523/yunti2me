package luoli523.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableEx {

  // Using Builder to build immutable collection
  private static ImmutableMap<String, Integer> WORD_TO_INT =
          new ImmutableMap.Builder<String, Integer>()
          .put("one", 1)
          .put("two", 2)
          .put("three", 3)
          .build();

  // Using of()
  private static ImmutableSet<String> KEYS = ImmutableSet.of("one", "two", "three");

}
