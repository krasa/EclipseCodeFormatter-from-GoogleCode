package krasa.formatter.settings.provider;

import krasa.formatter.common.ModifiableFile;
import krasa.formatter.plugin.InvalidPropertyFile;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.FileUtils;

import java.io.File;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class JSPropertiesProvider extends CachedPropertiesProvider {
    protected String profile;

	public JSPropertiesProvider(Settings settings) {
		super(new ModifiableFile(settings.getPathToConfigFileJS()));
        this.profile = settings.getSelectedJavaScriptProfile();
	}
    
    @Override
   	protected Properties readFile(File file) throws InvalidPropertyFile {
   		if (file.getName().endsWith("xml")) {
               return readXmlFile(file, profile);
   		} else {
   			// properties file
   			return super.readFile(file);
   		}
    }
}
