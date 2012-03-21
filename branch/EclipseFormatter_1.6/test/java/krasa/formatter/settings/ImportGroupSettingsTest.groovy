package krasa.formatter.settings


/**
 * @author Vojtech Krasa
 */
class ImportGroupSettingsTest extends GroovyTestCase {

    void testContains() {
        def settings = new ImportGroupSettings("com-java-javax-exe; foo-boo-cool; cz-com");
        assertTrue(settings.contains(new JoinedGroup("cz", "com")));
        assertTrue(settings.contains(new JoinedGroup("java", "com")));
        assertTrue(settings.contains(new JoinedGroup("javax", "com")));
        assertTrue(settings.contains(new JoinedGroup("java", "exe")));
        assertTrue(settings.contains(new JoinedGroup("javax", "java")));
        assertFalse(settings.contains(new JoinedGroup("foo", "cool1")));
        assertFalse(settings.contains(new JoinedGroup("cz", "1com")));
    }

}
