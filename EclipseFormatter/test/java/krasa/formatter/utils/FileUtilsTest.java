package krasa.formatter.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class FileUtilsTest {
	@Test
	public void testGetProfileNamesFromConfigXML() throws Exception {
		File file = new File("test/resources/format.xml");
		System.err.println(file.getAbsolutePath());
		List<String> profileNamesFromConfigXML = FileUtils.getProfileNamesFromConfigXML(file);
		Assert.assertFalse(profileNamesFromConfigXML.isEmpty());
	}
}
