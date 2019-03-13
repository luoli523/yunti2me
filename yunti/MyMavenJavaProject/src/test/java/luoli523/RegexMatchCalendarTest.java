package luoli523;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class RegexMatchCalendarTest
        extends TestCase {
    private static String pattern4 = "^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)((0[0-9])|(1[0-9])|(2[0-3]))[0-5][0-9][0-5][0-9]{1}";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RegexMatchCalendarTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(RegexMatchCalendarTest.class);
    }

    public void testCalendar() {
        String str1 = "20151213090845";
        String str2 = "30151213090845";
        String str3 = "20151313090845";
        String str4 = "20151243090845";
        String str5 = "20151213390845";
        String str6 = "20151213096845";
        String str7 = "20151213090865";
        assertTrue(RegexMatchCalendar.isValid(str1, pattern4));
        assertTrue(RegexMatchCalendar.isValid(str2, pattern4));
        assertFalse(RegexMatchCalendar.isValid(str3, pattern4));
        assertFalse(RegexMatchCalendar.isValid(str4, pattern4));
        assertFalse(RegexMatchCalendar.isValid(str5, pattern4));
        assertFalse(RegexMatchCalendar.isValid(str6, pattern4));
        assertFalse(RegexMatchCalendar.isValid(str7, pattern4));
    }
}
