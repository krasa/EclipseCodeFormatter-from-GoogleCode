package krasa.formatter.eclipse;

import junit.framework.Assert;
import krasa.formatter.settings.Settings;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class EclipseCodeFormatterFacadeTest {


    public static final String INPUT = "public class EclipseCodeFormatterFacadeTest {\n" +
            "\n" +
            "\tpublic static final String INPUT = \"ღმ⠀⠑⠁⠞色は匂へど 散りぬるを⠀⠛⠇⠁⠎⠎⠀⠁⠝⠙⠀⠊⠞⠀⠙⠕⠑⠎⠝⠞⠀⠓⠥⠗⠞⠀⠍⠑ერთსი შემვედრე, ნუთუ კვლა დამხსნას სოფლისა შრომასა, ცეცხლს, წყალსა და მიწასა, ჰაერთა თანა მრომასა; მომცნეს ფრთენი და აღვფრინდე, მივჰხვდე მას ჩემსა ნდომასა, დღისით და ღამით ვჰხედვიდე யாமறிந்த மொழிகளி+ěščřრუსთაველიžýáíé=ê¹ś¿źæñ³ó\";\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void testFormat() throws Exception {\n" +
            "\t\tString pathToCo色は匂へどnfigFile = \"org.eclipse.jdt.core.prefs\";\n" +
            "\t\tEclipseCodeFormatterFacade eclipseCodeFormatterFacade = new EclipseCodeFormatterFacade(pathToConfigFile);\n" +
            "\t\tString output = eclipseCodeFor⠎⠎⠀⠁⠝⠙matterFacade.format(INPUT, Settings.LINE_SEPARATOR);\n" +
            "\t\tAssert.assertEquals(INPUT, output);\n" +
            "\n" +
            "\t}\n" +
            "}";

    @Test
    public void testFormat() throws Exception {
        String pathToConfigFile = "test\\resources\\org.eclipse.jdt.core.prefs";
        JavaCodeFormatterFacade eclipseCodeFormatterFacade = new JavaCodeFormatterFacade(pathToConfigFile);
        String output = eclipseCodeFormatterFacade.format(INPUT, Settings.LINE_SEPARATOR);
        Assert.assertEquals(INPUT, output);
//        System.err.println(convert(INPUT, "cp1250"));
//        System.err.println(convert(INPUT, "utf8"));

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
