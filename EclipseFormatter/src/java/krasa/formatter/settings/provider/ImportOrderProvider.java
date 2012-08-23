package krasa.formatter.settings.provider;

import krasa.formatter.common.ModifiableFile;
import krasa.formatter.plugin.InvalidPropertyFile;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.FileUtils;
import krasa.formatter.utils.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class ImportOrderProvider extends CachedProvider<List<String>> {


	public ImportOrderProvider(Settings settings) {
		super(new ModifiableFile(settings.getImportOrderConfigFilePath()));
	}

	@Override
	protected List<String> readFile(File file) {
		List<String> strings;
		Properties properties = FileUtils.readPropertiesFile(file);
		String property = properties.getProperty("org.eclipse.jdt.ui.importorder");
		if (property == null) {
			throw new InvalidPropertyFile("org.eclipse.jdt.ui.importorder", file);
		}
		strings = StringUtils.trimToList(property);
		return strings;
	}

}
