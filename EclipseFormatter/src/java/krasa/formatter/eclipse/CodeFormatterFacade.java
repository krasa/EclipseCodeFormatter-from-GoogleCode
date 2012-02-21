package krasa.formatter.eclipse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public abstract class CodeFormatterFacade {
    public abstract String format(String content, String lineSeparator) throws InvalidPathToConfigFileException;

    public abstract String format(String text, int startOffset, int endOffset, String lineSeparator)
            throws InvalidPathToConfigFileException;

    /**
     * Return a Java Properties file representing the options that are in the specified configuration file.
     */
    protected Properties readConfig(File file) {
        BufferedInputStream stream = null;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            final Properties formatterOptions = new Properties();
            formatterOptions.load(stream);
            return formatterOptions;
        } catch (IOException e) {
            throw new RuntimeException("config file read error", e);
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
