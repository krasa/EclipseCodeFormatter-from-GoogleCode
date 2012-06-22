package krasa.formatter.settings.provider;

import krasa.formatter.common.ModifiableFile;
import krasa.formatter.plugin.InvalidPropertyFile;
import krasa.formatter.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class CachedPropertiesProvider {
    protected ModifiableFile modifiableFile;
    protected Properties properties;

    public Properties getProperties() {
        if (properties == null || modifiableFile.wasChanged()) {
            modifiableFile.saveLastModified();
            properties = readConfig(modifiableFile);
        }
        if (properties.isEmpty()) {
            throw new InvalidPropertyFile(modifiableFile);
        }
        return properties;
    }

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

    protected void trimTrailingWhitespaceFromConfigValues(Properties config) {
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

    public boolean wasChanged() {
        return modifiableFile.wasChanged();
    }
}
