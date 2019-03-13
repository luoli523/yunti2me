package luoli523;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatchCalendar {

    private static String pattern4 = "^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)((0[0-9])|(1[0-9])|(2[0-3]))[0-5][0-9][0-5][0-9]{1}";

    public static boolean isValid(String timeStr, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(timeStr);
        if (m.matches())
            return true;
        return false;
    }

    public static void main(String[] args) {
        String str1 = "20151213090845";
        String str2 = "30151213090845";
        String str3 = "20151313090845";
        String str4 = "20151243090845";
        String str5 = "20151213390845";
        String str6 = "20151213096845";
        String str7 = "20151213090865";
        System.out.println(isValid(str1, pattern4));
        System.out.println(isValid(str2, pattern4));
        System.out.println(isValid(str3, pattern4));
        System.out.println(isValid(str4, pattern4));
        System.out.println(isValid(str5, pattern4));
        System.out.println(isValid(str6, pattern4));
        System.out.println(isValid(str7, pattern4));
    }
}
