package luoli523.com;

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
