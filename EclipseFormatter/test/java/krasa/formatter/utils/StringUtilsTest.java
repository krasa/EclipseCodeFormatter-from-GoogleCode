package krasa.formatter.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class StringUtilsTest {
    @Test
    public void testBetterMatching() throws Exception {
        String order1 = "com.foo";
        String order2 = "com.kuk";
        String s = StringUtils.betterMatching(order2, order1, "com.foo.goo");
        Assert.assertEquals(order1, s);
        s = StringUtils.betterMatching(order1, order2, "com.foo.goo");
        Assert.assertEquals(order1, s);
    }
}
