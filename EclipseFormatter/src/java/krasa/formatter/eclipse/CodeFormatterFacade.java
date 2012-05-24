package krasa.formatter.eclipse;

import krasa.formatter.plugin.InvalidPropertyFile;
import krasa.formatter.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public abstract class CodeFormatterFacade {


    public String format(String content) throws FileDoesNotExistsException {
        return formatInternal(content, 0, content.length());
    }

    /**
     * @param text        to format
     * @param startOffset start of formatted area - this should be always start of line
     * @param endOffset   end of formatted area
     */
    public String format(String text, int startOffset, int endOffset) throws FileDoesNotExistsException {
        return formatInternal(text, startOffset, endOffset);
    }

    protected abstract String formatInternal(String text, int startOffset, int endOffset) throws FileDoesNotExistsException;


    /**
     * Return a Java Properties object representing the options that are in the specified configuration file.
     */
    protected Properties readConfig(File file) throws InvalidPropertyFile {
        final Properties formatterOptions = FileUtils.readPropertiesFile(file, createDefaultConfig());
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

}
