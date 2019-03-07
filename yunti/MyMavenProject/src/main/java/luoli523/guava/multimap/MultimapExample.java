package luoli523.guava.multimap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MultimapExample {
  private static final String COLOR = "Color";
  private static final String RED = "Red";
  private static final String YELLOW = "Yellow";
  private static final String GREEN = "Green";

  private static final String FURNITURE = "Furniture";
  private static final String TABLE = "Table";
  private static final String CHAIR = "Chair";
  private static final String SOFA = "Sofa";

  public static void main(String[] args) {
    Multimap<String, String> bag = ArrayListMultimap.create();

    bag.put(COLOR, RED);
    bag.put(COLOR, RED);
    bag.put(COLOR, YELLOW);
    bag.put(COLOR, GREEN);
    bag.put(COLOR, GREEN);
    bag.put(COLOR, GREEN);

    bag.put(FURNITURE, TABLE);
    bag.put(FURNITURE, CHAIR);
    bag.put(FURNITURE, SOFA);

    System.out.println("Total items in Bag: " + bag.size());
    System.out.println("Total colors in Bag: " + bag.get(COLOR).size());
    System.out.println("Colors in Bag: " + bag.get(COLOR));

    System.out.println("Total items in furniture: " + bag.get(FURNITURE).size());
    System.out.println("Furniture Items: " + bag.get(FURNITURE));
  }
}
