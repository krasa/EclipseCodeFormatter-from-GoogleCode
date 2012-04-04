package krasa.formatter.utils;

import com.intellij.openapi.project.Project;
import krasa.formatter.settings.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class StringUtils {

    public static String betterMatching(String order1, String order2, String anImport) {
        if (order1.equals(order2)) {
            throw new IllegalArgumentException("orders are same");
        }
        for (int i = 0; i < anImport.length() - 1; i++) {
            if (order1.length() - 1 == i && order2.length() - 1 != i) {
                return order2;
            }
            if (order2.length() - 1 == i && order1.length() - 1 != i) {
                return order1;
            }
            char orderChar1 = order1.charAt(i);
            char orderChar2 = order2.charAt(i);
            char importChar = anImport.charAt(i);

            if (importChar == orderChar1 && importChar != orderChar2) {
                return order1;
            } else if (importChar != orderChar1 && importChar == orderChar2) {
                return order2;
            }

        }
        return null;
    }

    public static List<String> trimToList(String importOrder1) {
        ArrayList<String> strings = new ArrayList<String>();
        String[] split = importOrder1.split(";");
        for (String s : split) {
            String trim = s.trim();
            if (!trim.isEmpty()) {
                strings.add(trim);
            }
        }
        return strings;
    }

    public static List<String> trimImport(String imports) {
        String[] split = imports.split("\n");
        ArrayList<String> strings = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (s.startsWith("import ")) {
                s = s.substring(7, s.indexOf(";"));
                strings.add(s);
            }
        }
        return strings;
    }

    public static String generateName(List<Settings> settingsList, Project project, int i, String name) {
        for (Settings settings : settingsList) {
            if (name.equals(settings.getName())) {
                name = project.getName() + " (" + i + ")";
                name = generateName(settingsList, project, ++i, name);
            }
        }
        return name;
    }
}
