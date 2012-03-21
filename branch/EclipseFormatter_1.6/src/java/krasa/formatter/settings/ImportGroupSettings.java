package krasa.formatter.settings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class ImportGroupSettings {

    private final List<JoinedGroup> groups;

    public ImportGroupSettings(String group1, String group2) {
        groups = new ArrayList<JoinedGroup>(1);
        groups.add(new JoinedGroup(group1, group2));
    }

    /**
     * example: com-org-eu-net; cz-com;
     */
    public ImportGroupSettings(@NotNull String groupsString) {
        this.groups = new ArrayList<JoinedGroup>();
        for (String group : groupsString.split(";")) {
            String[] split = group.split("-");
            if (split.length < 2) {
                continue;
            }
            groups.add(new JoinedGroup(split));
        }
    }

    private ImportGroupSettings() {
        groups = Collections.emptyList();
    }

    public boolean contains(@NotNull JoinedGroup that) {
        for (JoinedGroup group : groups) {
            if (group.contains(that)) {
                return true;
            }
        }
        return false;
    }

    public static ImportGroupSettings empty() {
        return new ImportGroupSettings();
    }
}
