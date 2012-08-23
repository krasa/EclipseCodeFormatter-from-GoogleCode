package krasa.formatter.settings.provider;

import com.google.gwt.dev.util.Util;
import com.intellij.openapi.util.io.FileUtil;
import com.yourkit.util.Asserts;
import junit.framework.Assert;
import krasa.formatter.common.ModifiableFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Vojtech Krasa
 */
public class CachedProviderTest {

	protected CachedProvider<String> cachedProvider;
	protected File tempFile;

	@Before
	public void setUp() throws Exception {
		tempFile = File.createTempFile("12311", "2");
		cachedProvider = new CachedProvider<String>(new ModifiableFile(tempFile.getPath())) {

			@Override
			protected String readFile(File file) {
				return Util.readFileAsString(file);
			}
		};
	}

	@Test
	public void testWasChanged() throws Exception {
		ModifiableFile.Monitor modifiedMonitor = cachedProvider.getModifiedMonitor();
		Asserts.assertFalse(cachedProvider.wasChanged(modifiedMonitor));
		tempFile.setLastModified(tempFile.lastModified() + 1000);
		Asserts.assertTrue(cachedProvider.wasChanged(modifiedMonitor));
	}

	@Test
	public void testGet() throws Exception {
		FileUtil.writeToFile(tempFile, "foo");
		tempFile.setLastModified(1000);
		String s = cachedProvider.get();
		Assert.assertEquals("foo", s);

		FileUtil.writeToFile(tempFile, "bar");

		tempFile.setLastModified(1000);
		s = cachedProvider.get();
		Assert.assertEquals("foo", s);

		tempFile.setLastModified(2000);
		s = cachedProvider.get();
		Assert.assertEquals("bar", s);
	}
}
