package krasa.formatter.plugin.processor;

import junit.framework.Assert;
import krasa.formatter.common.ModifiableFile;
import krasa.formatter.plugin.ImportSorter;
import krasa.formatter.settings.Settings;
import krasa.formatter.settings.provider.ImportOrderProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

import java.util.List;

import static org.easymock.EasyMock.expect;
//import static org.easymock.EasyMock.*;


/**
 * @author Vojtech Krasa
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ImportOrderProcessorTest {
	@Mock
	protected ImportSorter importSorter;
	@Mock
	protected ImportOrderProvider orderProvider;
	@Mock
	protected Settings settings;
	@Mock
	protected List<String> importOrder;
	@Mock
	protected ModifiableFile.Monitor monitor;

	@Test
	public void testInitializeImportSorter() throws Exception {
		expect(settings.isImportOrderFromFile()).andReturn(true);
		expect(orderProvider.wasChanged(null)).andReturn(true);
		expect(orderProvider.getModifiedMonitor()).andReturn(monitor);
		expect(orderProvider.get()).andReturn(importOrder);
		EasyMockUnitils.replay();

		ImportOrderProcessor importOrderProcessor = new ImportOrderProcessor(settings, null, orderProvider, null);


		ImportSorter importSorter = importOrderProcessor.getImportSorter();
		Assert.assertNotNull(importSorter);
		EasyMockUnitils.verify();

	}
}
