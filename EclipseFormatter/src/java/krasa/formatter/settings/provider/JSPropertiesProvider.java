package krasa.formatter.settings.provider;

import krasa.formatter.common.ModifiableFile;
import krasa.formatter.settings.Settings;

/**
 * @author Vojtech Krasa
 */
public class JSPropertiesProvider extends CachedPropertiesProvider {

	public JSPropertiesProvider(Settings settings) {
		super(new ModifiableFile(settings.getPathToConfigFileJS()));
	}
}
