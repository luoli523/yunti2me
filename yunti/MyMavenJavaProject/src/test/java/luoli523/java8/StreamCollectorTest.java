package luoli523.java8;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamCollectorTest {
  private List<Product> productList;

  @Before
  public void prepare() {
    productList = Arrays.asList(new Product(23, "potatoes"),
            new Product(14, "orange"), new Product(13, "lemon"),
            new Product(23, "bread"), new Product(13, "sugar"));
  }

  @Test
  public void convertStreamToCollection() {
    productList = Arrays.asList(new Product(23, "potatoes"),
            new Product(14, "orange"), new Product(13, "lemon"),
            new Product(23, "bread"), new Product(13, "sugar"));
    List<String> productNames = productList.stream().map(Product::getName).collect(Collectors.toList());
    assertEquals("[potatoes,orange,lemon,bread,sugar]",
            productNames.stream()
                    .collect(
                            Collectors.joining(",","[","]")
                    ));
  }

  @Test
  public void collectorWays() {
    double averagePrice = productList.stream()
            .collect(Collectors.averagingInt(Product::getPrice));
    int summingPrice = productList.stream()
            .collect(Collectors.summingInt(Product::getPrice));
    assertEquals(summingPrice, 86);
    System.out.println(averagePrice);
    System.out.println(summingPrice);
  }
}

class Product {
  private String name;
  private int price;

  public Product(int price, String name) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public int getPrice() {
    return price;
  }
}
