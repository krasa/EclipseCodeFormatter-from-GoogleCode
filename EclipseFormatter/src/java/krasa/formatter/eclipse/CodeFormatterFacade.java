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


    public String format(String content, String lineSeparator) throws InvalidPathToConfigFileException {
        return formatInternal(content, 0, content.length(), lineSeparator);
    }

    /**
     * @param text          to format
     * @param startOffset   start of formatted area - this should be always start of line
     * @param endOffset     end of formatted area
     * @param lineSeparator - null for default
     */
    public String format(String text, int startOffset, int endOffset, String lineSeparator) throws InvalidPathToConfigFileException {
        return formatInternal(text, startOffset, endOffset, lineSeparator);
    }

    protected abstract String formatInternal(String text, int startOffset, int endOffset, String lineSeparator) throws InvalidPathToConfigFileException;


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

    protected File checkIfExists(String pathToConfigFile1) throws InvalidPathToConfigFileException {
        File file = new File(pathToConfigFile1);
        if (!file.exists()) {
            System.err.println(new File("").getAbsolutePath());
            throw new InvalidPathToConfigFileException();
        }
        return file;
    }
}
