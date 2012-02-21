package krasa.formatter.eclipse;

import junit.framework.Assert;
import krasa.formatter.settings.Settings;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class JavaCodeFormatterFacadeTest {


    public static final String INPUT = "public class EclipseCodeFormatterFacadeTest {\n" +
            "\n" +
            "\t                 public static final String INPUT = \"ღმ⠀⠑⠁⠞色は匂へど 散りぬるを⠀⠛⠇⠁⠎⠎⠀⠁⠝⠙⠀⠊⠞⠀⠙⠕⠑⠎⠝⠞⠀⠓⠥⠗⠞⠀⠍⠑ერთსი შემვედრე, ნუთუ კვლა დამხსნას სოფლისა შრომასა, ცეცხლს, წყალსა და მიწასა, ჰაერთა თანა მრომასა; მომცნეს ფრთენი და აღვფრინდე, მივჰხვდე მას ჩემსა ნდომასა, დღისით და ღამით ვჰხედვიდე யாமறிந்த மொழிகளி+ěščřრუსთაველიžýáíé=ê¹ś¿źæñ³ó\";\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void testFormat() throws                      Exception {\n" +
            "\t\t           String pathToCo色は匂へどnfigFile = \"org.eclipse.jdt.core.prefs\";\n" +
            "\t\t                     EclipseCodeFormatterFacade          eclipseCodeFormatterFacade = new EclipseCodeFormatterFacade(pathToConfigFile);\n" +
            "\t\tString output                   =                 eclipseCodeFormatterFacade.format(INPUT, Settings.LINE_SEPARATOR);\n" +
            "\t\tAssert.                 assertEquals(INPUT,                 output);\n" +
            "\n" +
            "\t}\n" +
            "}";

    public static final String INPUT2 = "package krasa;\n" +
            "\n" +
            "import org.xml.sax.InputSource;\n" +
            "\n" +
            "\n" +
            "/**\n" +
            "                   *                         @author Vojtech Krasa\n" +
            "                  */\n" +
            "public            class Test2 {\n" +
            "\n" +
            "\t                       public static final InputSource INPUT_SOURCE = new InputSource();\n" +
            "\n" +
            "\tpublic                 static void main(String[]           args) {\n" +
            "\n" +
            "\t\tSystem.err.println(\"\" + \"\" + \"\" + \"\" + \"\");\n" +
            "\t\t          System.err.println(\"\" + \"\" + \"\" + \"\" + \"\");\n" +
            "\n" +
            "\t}\n" +
            "\n" +
            "}";

    public static final String FORMATTED = "public class EclipseCodeFormatterFacadeTest {\n" +
            "\n" +
            "\tpublic static final String INPUT = \"ღმ⠀⠑⠁⠞色は匂へど 散りぬるを⠀⠛⠇⠁⠎⠎⠀⠁⠝⠙⠀⠊⠞⠀⠙⠕⠑⠎⠝⠞⠀⠓⠥⠗⠞⠀⠍⠑ერთსი შემვედრე, ნუთუ კვლა დამხსნას სოფლისა შრომასა, ცეცხლს, წყალსა და მიწასა, ჰაერთა თანა მრომასა; მომცნეს ფრთენი და აღვფრინდე, მივჰხვდე მას ჩემსა ნდომასა, დღისით და ღამით ვჰხედვიდე யாமறிந்த மொழிகளி+ěščřრუსთაველიžýáíé=ê¹ś¿źæñ³ó\";\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void testFormat() throws Exception {\n" +
            "\t\tString pathToCo色は匂へどnfigFile = \"org.eclipse.jdt.core.prefs\";\n" +
            "\t\tEclipseCodeFormatterFacade eclipseCodeFormatterFacade = new EclipseCodeFormatterFacade(pathToConfigFile);\n" +
            "\t\tString output = eclipseCodeFormatterFacade.format(INPUT, Settings.LINE_SEPARATOR);\n" +
            "\t\tAssert.assertEquals(INPUT, output);\n" +
            "\n" +
            "\t}\n" +
            "}";
    public static final String FORMATTED2 = "package krasa;\n" +
            "\n" +
            "import org.xml.sax.InputSource;\n" +
            "\n" +
            "/**\n" +
            " * @author Vojtech Krasa\n" +
            " */\n" +
            "public class Test2 {\n" +
            "\n" +
            "\tpublic static final InputSource INPUT_SOURCE = new InputSource();\n" +
            "\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\n" +
            "\t\tSystem.err.println(\"\" + \"\" + \"\" + \"\" + \"\");\n" +
            "\t\tSystem.err.println(\"\" + \"\" + \"\" + \"\" + \"\");\n" +
            "\n" +
            "\t}\n" +
            "\n" +
            "}";


    public static final String PATH_TO_CONFIG_FILE = "test\\resources\\org.eclipse.jdt.core.prefs";
    protected JavaCodeFormatterFacade eclipseCodeFormatterFacade;

    @Before
    public void setUp() throws Exception {
        eclipseCodeFormatterFacade = new JavaCodeFormatterFacade(PATH_TO_CONFIG_FILE);
    }

    @Test
    public void testFormat() throws Exception {
        String output = eclipseCodeFormatterFacade.format(INPUT, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED, output);
        output = eclipseCodeFormatterFacade.format(INPUT2, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED2, output);
    }

    @Test
    public void testFormat2() throws Exception {
        String input2 = INPUT2;
        String output = eclipseCodeFormatterFacade.format(input2, 10, input2.length() - 10, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED2, output);
    }

    @Test
    public void testFormat3() throws Exception {
        String input2 = FORMATTED2;
        String output = eclipseCodeFormatterFacade.format(input2, input2.length() - 20, input2.length() - 10, Settings.LINE_SEPARATOR);
        Assert.assertEquals(FORMATTED2, output);
        input2 = INPUT;
        eclipseCodeFormatterFacade.format(input2, input2.length() - 20, input2.length() - 10, Settings.LINE_SEPARATOR);
    }

    public String convert(String s, String utf8) throws UnsupportedEncodingException {
        return new String(s.getBytes("UTF8"), utf8);
    }


    Properties readConfig(File file) {
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            final Properties formatterOptions = new Properties();
            formatterOptions.load(stream);
            return formatterOptions;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

}
