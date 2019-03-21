package luoli523;

public class StringTest {
    public static void main(String[] args) {
        String str = "hello";
        func(str);
        System.out.println(str);
    }

    private static void func(String str) {
        str += " world";
    }
}
