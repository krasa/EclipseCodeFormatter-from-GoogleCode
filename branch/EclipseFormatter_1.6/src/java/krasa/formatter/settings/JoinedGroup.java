package krasa.formatter.settings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class JoinedGroup {

    private final List<String> groups;

    public JoinedGroup(@NotNull String... param) {
        groups = new ArrayList<String>();
        for (String s : param) {
            groups.add(s.trim());
        }
    }

    public boolean contains(JoinedGroup that) {
        for (String s : that.getGroups()) {
            if (!groups.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getGroups() {
        return groups;
    }
}
