package luoli523.java8;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

public class OptionalTest {

  @Test(expected = NoSuchElementException.class)
  public void emptyOptionalGetWillThrowException() {
    Optional<String> empty = Optional.empty();
    empty.get();
  }
}
