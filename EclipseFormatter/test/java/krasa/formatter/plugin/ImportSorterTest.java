package krasa.formatter.plugin;

import krasa.formatter.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class ImportSorterTest {

	public static final String N = "\n";
	public static final List<String> DEFAULT_ORDER = Arrays.asList("java", "javax", "org", "com");

	@Test
	public void testName() throws Exception {
		String EXPECTED = "import java.util.HashMap;\n" + "import java.util.Map;\n" + "\n"
				+ "import org.jingle.mocquer.MockControl;\n" + N + "import sun.security.action.GetLongAction;\n"
				+ "import tmplatform.authorisation.ApiClientLink;\n"
				+ "import tmplatform.comms.common.caaa.EvasLdapInterfaceProfileWrapper;\n"
				+ "import base.LoadUnitTestDataTestCase;\n" + N + "import com.sun.rmi.rmid.ExecOptionPermission;" + N;

		List<String> imports = Arrays.asList("java.util.HashMap",
				"tmplatform.comms.common.caaa.EvasLdapInterfaceProfileWrapper",
				"com.sun.rmi.rmid.ExecOptionPermission", "java.util.Map", "base.LoadUnitTestDataTestCase",
				"org.jingle.mocquer.MockControl", "tmplatform.authorisation.ApiClientLink",
				"sun.security.action.GetLongAction");

		List<String> importsOrder = Arrays.asList("java", "javax", "org", "com");
		ImportSorter importSorter = new ImportSorter(importsOrder);
		List<String> strings = importSorter.sortByEclipseStandard(imports);
		printAndAssert(EXPECTED, strings);
	}

	@Test
	public void testName2() throws Exception {
		String EXPECTED = "import static com.MyUtil.kuk;\n" + "import static org.junit.Assert.assertNotNull;\n"
				+ "import static tmutil.StringUtil.replaceText;\n\n" + "import java.util.HashMap;\n"
				+ "import java.util.Map;\n" + "\n" + "import org.jingle.mocquer.MockControl;\n" + N
				+ "import sun.security.action.GetLongAction;\n" + "import tmplatform.authorisation.ApiClientLink;\n"
				+ "import tmplatform.comms.common.caaa.EvasLdapInterfaceProfileWrapper;\n"
				+ "import base.LoadUnitTestDataTestCase;\n";

		String imports = "import static com.MyUtil.kuk;\n" + "import java.util.Map;\n" + "\n"
				+ "import static org.junit.Assert.assertNotNull;\n"
				+ "import tmplatform.authorisation.ApiClientLink;\n" + "import java.util.HashMap;\n"
				+ "import sun.security.action.GetLongAction;\n" + "import org.jingle.mocquer.MockControl;\n"
				+ "import tmplatform.comms.common.caaa.EvasLdapInterfaceProfileWrapper;\n" + N
				+ "import static tmutil.StringUtil.replaceText;\n\n" + "import base.LoadUnitTestDataTestCase;\n";

		List<String> importsOrder = Arrays.asList("java", "javax", "org", "com");
		ImportSorter importSorter = new ImportSorter(importsOrder);
		List<String> strings = importSorter.sortByEclipseStandard(StringUtils.trimImport(imports));
		printAndAssert(EXPECTED, strings);
	}

	@Test
	public void testName3() throws Exception {
		String EXPECTED = "import static com.MyUtil.kuk;\n" + "import static org.junit.Assert.assertNotNull;\n"
				+ "import static tmutil.StringUtil.replaceText;\n\n" + "import java.util.ArrayList;\n" + "\n"
				+ "import org.w3c.dom.DOMConfiguration;\n" + "import org.w3c.dom.DOMException;\n"
				+ "import org.w3c.dom.Document;\n" + "import org.w3c.dom.Node;\n"
				+ "import org.w3c.dom.ls.LSException;\n" + "import org.w3c.dom.ls.LSInput;\n"
				+ "import org.w3c.dom.ls.LSParser;\n" + "import org.w3c.dom.ls.LSParserFilter;\n"
				+ "import org.xml.sax.InputSource;\n";

		String imports = "import org.w3c.dom.DOMConfiguration;\n" + "import org.w3c.dom.DOMException;\n"
				+ "import org.w3c.dom.Document;\n" + "import org.w3c.dom.Node;\n"
				+ "import org.w3c.dom.ls.LSException;\n" + "import org.w3c.dom.ls.LSInput;\n"
				+ "import static com.MyUtil.kuk;\n" + "import static org.junit.Assert.assertNotNull;\n"
				+ "import static tmutil.StringUtil.replaceText;\n" + "import org.w3c.dom.ls.LSParser;\n"
				+ "import org.w3c.dom.ls.LSParserFilter;\n" + "import org.xml.sax.InputSource;\n" + N
				+ "import java.util.ArrayList;";

		List<String> importsOrder = Arrays.asList("java", "javax", "org", "com");
		ImportSorter importSorter = new ImportSorter(importsOrder);

		List<String> imports1 = StringUtils.trimImport(imports);
		System.err.println(Arrays.toString(imports1.toArray()));
		List<String> strings = importSorter.sortByEclipseStandard(imports1);
		printAndAssert(EXPECTED, strings);
	}

	@Test
	public void testName4() throws Exception {
		String EXPECTED = "import java.util.Arrays;\n" +
				"\n" +
				"import models.Deployment;\n" +
				"import play.jobs.Job;\n" +
				"import play.mvc.Before;\n" +
				"import controllers.deadbolt.Restrict;\n";

		String imports = "import controllers.deadbolt.Restrict;\n" +
				"import java.util.Arrays;\n" +
				"import play.mvc.Before;\n" +
				"import models.Deployment;\n" +
				"import play.jobs.Job;\n";

		List<String> importsOrder = DEFAULT_ORDER;
		ImportSorter importSorter = new ImportSorter(importsOrder);

		List<String> imports1 = StringUtils.trimImport(imports);
		System.err.println(Arrays.toString(imports1.toArray()));
		List<String> strings = importSorter.sortByEclipseStandard(imports1);
		printAndAssert(EXPECTED, strings);
	}

	@Test
	public void testName5() throws Exception {
		String EXPECTED =
				"import static java.lang.Integer.numberOfLeadingZeros;\n" +
						"import static java.lang.Integer.valueOf;\n" +
						"\n" +
						"import java.sql.Date;\n" +
						"import java.util.List;\n" +
						"import java.util.Map;\n" +
						"import javax.xml.crypto.dsig.spec.HMACParameterSpec;\n" +
						"import org.w3c.dom.Text;\n" +
						"import org.w3c.dom.stylesheets.StyleSheetList;\n";

		String imports =
				"import javax.xml.crypto.dsig.spec.HMACParameterSpec;\n" +
						"import org.w3c.dom.Text;\n" +
						"import java.util.List;\n" +
						"import static java.lang.Integer.numberOfLeadingZeros;\n" +
						"import java.sql.Date;\n" +
						"\n" +
						"import static java.lang.Integer.valueOf;\n" +
						"import java.util.Map;\n" +
						"import org.w3c.dom.stylesheets.StyleSheetList;\n";

		List<String> importsOrder = Collections.emptyList();
		ImportSorter importSorter = new ImportSorter(importsOrder);

		List<String> imports1 = StringUtils.trimImport(imports);
		System.err.println(Arrays.toString(imports1.toArray()));
		List<String> strings = importSorter.sortByEclipseStandard(imports1);
		printAndAssert(EXPECTED, strings);
	}

	private void printAndAssert(String EXPECTED, List<String> strings) {
		StringBuilder stringBuilder = print(strings);
		System.out.println("-----EXPECTED------");
		System.out.println(EXPECTED);
		Assert.assertEquals(EXPECTED, stringBuilder.toString());
		System.out.println("-----------------");

	}

	private StringBuilder print(List<String> strings) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : strings) {
			stringBuilder.append(string);
		}

		System.out.println(stringBuilder.toString());
		return stringBuilder;
	}
}
