package krasa.formatter.eclipse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
     * Return a Java Properties object representing the options that are in the specified configuration file.
     */
    protected Properties readConfig(File file) {
        BufferedInputStream stream = null;
        final Properties formatterOptions;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            formatterOptions = new Properties(createDefaultConfig());
            formatterOptions.load(stream);
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
        // Properties.load() does not trim trailing whitespace from prop values, so trim it ourselves, since it would
        // cause the Eclipse formatter to fail to parse the values.
        trimTrailingWhitespaceFromConfigValues(formatterOptions);
        validateConfig(formatterOptions);

        return formatterOptions;
    }

    private void trimTrailingWhitespaceFromConfigValues(Properties config) {
        // First trim the values and store the trimmed values in a temporary map.
        Map<String, String> map = new HashMap<String, String>(config.size());
        for (Object key : config.keySet()) {
            String optionName = (String) key;
            String optionValue = config.getProperty(optionName);
            map.put(optionName, (optionValue != null) ? optionValue.trim() : null);
        }
        // Then copy the values back to the original Properties object.
        for (String key : map.keySet()) {
            config.setProperty(key, map.get(key));
        }
    }

    protected void validateConfig(Properties config) {
        return;
    }

    protected Properties createDefaultConfig() {
        return null;
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
