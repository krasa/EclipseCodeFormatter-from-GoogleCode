package krasa.formatter.eclipse;

import junit.framework.Assert;
import krasa.formatter.settings.Settings;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class JSCodeFormatterFacadeTest {

    public static final String JS = "/**\n" +
            " * Wrapper for java.lang.Object.wait\n" +
            " *\n" +
            " * can be called only within a sync method\n" +
            " */\n" +
            "function wait(object) {\n" +
            "    var objClazz = java.lang.Class.forName('java.lang.Object');\n" +
            "    var waitMethod = objClazz.getMethod('wait', null);\n" +
            "    waitMethod.invoke(object, null);\n" +
            "}\n" +
            "wait.docString = \"convenient wrapper for java.lang.Object.wait method\";";

    public static final String FORMATTED_JS = "/**\n" +
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

    @Test
    public void testFormat() throws Exception {
        String pathToConfigFile = "test\\resources\\org.eclipse.wst.jsdt.core.prefs";
        CodeFormatterFacade eclipseCodeFormatterFacade = new JSCodeFormatterFacade(pathToConfigFile);
        String output = eclipseCodeFormatterFacade.format(JS, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED_JS, output);
    }
}
