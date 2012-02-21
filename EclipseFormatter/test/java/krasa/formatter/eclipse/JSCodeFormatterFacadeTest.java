package krasa.formatter.eclipse;

import junit.framework.Assert;
import krasa.formatter.settings.Settings;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class JSCodeFormatterFacadeTest {

    public static final String INPUT = "/**\n" +
            " * Wrapper for java.lang.Object.wait\n" +
            "       *\n" +
            "       * can be called only within a sync method\n" +
            " */\n" +
            "function wait(object) {\n" +
            "                 var objClazz = java.lang.Class.forName('java.lang.Object');\n" +
            "    var waitMethod = objClazz.getMethod('wait', null);\n" +
            "    waitMethod.invoke(object, null);\n" +
            "} \n" +
            "wait.docString = \"convenient wrapper for java.lang.Object.wait method\";";

    public static final String FORMATTED = "/**\n" +
            " * Wrapper for java.lang.Object.wait\n" +
            " *\n" +
            " * can be called only within a sync method\n" +
            " */\n" +
            "function wait(object) {\n" +
            "\tvar objClazz = java.lang.Class.forName('java.lang.Object');\n" +
            "\tvar waitMethod = objClazz.getMethod('wait', null);\n" +
            "\twaitMethod.invoke(object, null);\n" +
            "}\n" +
            "wait.docString = \"convenient wrapper for java.lang.Object.wait method\";";


    protected CodeFormatterFacade eclipseCodeFormatterFacade = new JSCodeFormatterFacade(PATH_TO_CONFIG_FILE);
    public static final String PATH_TO_CONFIG_FILE = "test\\resources\\org.eclipse.wst.jsdt.core.prefs";

    @Test
    public void testFormat() throws Exception {
        String output = eclipseCodeFormatterFacade.format(INPUT, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED, output);
    }

    @Test
    public void testFormat2() throws Exception {
        String output = eclipseCodeFormatterFacade.format(INPUT, 10, INPUT.length() - 10, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED, output);
    }

}
