package luoli523.backtobasic;

import org.jcodings.util.IntArrayHash;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrimitivesAndNonPrimitivesPassTest {

  @Test
  public void whenModifyingPrimitives_thenOriginalValuesNotModified() {
    int a = 1;
    int b = 1;

    assertEquals(a, 1);
    assertEquals(b, 1);

    modifyPrimitive(a, b);

    assertEquals(a, 1);
    assertEquals(b, 1);
  }

  @Test
  public void whenModifyingObjects_thenOriginalObjectChanged() {
    Num a = new Num(1);
    Num b = new Num(1);

    assertEquals(a.x, 1);
    assertEquals(b.x, 1);

    modifyNonPrimitive(a, b);

    assertEquals(a.x, 2);
    assertEquals(b.x, 1);
  }

  @Test
  public void whenModifyingPrimitiveCovers_thenOriginalObjectNotChanged() {
    Integer a = 1;
    Integer b = 1;

    assertEquals(a.intValue(), 1);
    assertEquals(b.intValue(), 1);

    modifyPrimitiveCover(a, b);

    // modifyPrimitiveCover()中对a = a + 1, 实际上a指向了一个新的对象
    assertEquals(a.intValue(), 1);
    // b++ 的逻辑也跟b = b+1 一样
    assertEquals(b.intValue(), 1);
  }

  void modifyPrimitive(int a, int b){
    a = 2;
    b = 3;
  }

  void modifyNonPrimitive(Num a, Num b){
    a.x++;

    b = new Num(1);
    b.x++;
  }

  void modifyPrimitiveCover(Integer a, Integer b) {
    a = a + 1;
    b++;
  }

  class Num {
    public int x;
    public Num(int x) {
      this.x = x;
    }
  }
}
