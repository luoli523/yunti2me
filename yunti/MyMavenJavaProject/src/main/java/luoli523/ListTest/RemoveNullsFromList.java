package luoli523.ListTest;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RemoveNullsFromList {

  public static void main(String[] args) {
    List<Integer> list = Lists.newArrayList(null, 1 , null);
    while (list.remove(null));
    System.out.println(list);

    // using guava iterables
    Collections.shuffle(list);
    Iterables.removeIf(list, Predicates.<Integer>isNull());
    System.out.println(list);

    // using apache collections
    Collections.shuffle(list);
    CollectionUtils.filter(list, PredicateUtils.notNullPredicate());
    System.out.println(list);

    // using java8 lambda
    Collections.shuffle(list);
    /*
    List<String> listWithoutNull = list.parallelStream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    System.out.println(list);
    */
  }
}
