package luoli523.linkedHashMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class FiveEntryLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

  private static final int MAX_ENTRIES = 5;

  public FiveEntryLinkedHashMap(int initalCapacity, float loadFactor, boolean accessOrder) {
    super(initalCapacity, loadFactor, accessOrder);
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > MAX_ENTRIES;
  }
}

public class LinkedHashMapRemoveEldestEntry {

  public static void main(String[] args) {
    LinkedHashMap<Integer, String> map
            = new FiveEntryLinkedHashMap<Integer, String>(16, .75f, true);
    map.put(1, null);
    map.put(2, null);
    map.put(3, null);
    map.put(4, null);
    map.put(5, null);
    Set<Integer> keys = map.keySet();
    System.out.println(keys); // 1,2,3,4,5

    map.put(6, null);
    System.out.println(keys); // 2,3,4,5,6
    map.get(3);
    System.out.println(keys); // 2,4,5,6,3

    map.put(7, null);
    System.out.println(keys); // 4,5,6,3,7
    map.put(8, null);
    System.out.println(keys); // 5,6,3,7,8
  }

}
