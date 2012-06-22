package krasa.formatter.settings.provider;

import krasa.formatter.common.ModifiableFile;

/**
 * @author Vojtech Krasa
 */
public class JSPropertiesProvider extends CachedPropertiesProvider {

    public JSPropertiesProvider(String pathToConfigFile) {
        modifiableFile = new ModifiableFile(pathToConfigFile);
    }
}
