package krasa.formatter.settings;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
@State(
        name = "EclipseCodeFormatterSettings",
        storages = {
                @Storage(
                        file = "$APP_CONFIG$/eclipseCodeFormatter.xml"
                )}
)
public class GlobalSettings implements ApplicationComponent, PersistentStateComponent<GlobalSettings>,ExportableApplicationComponent {
    private List<Settings> settingsList = new ArrayList<Settings>();
    private Integer lastId = 0;

    public static GlobalSettings getInstance() {
        return ServiceManager.getService(GlobalSettings.class);
    }

    public GlobalSettings getState() {
        return this;
    }

    public void loadState(GlobalSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<Settings> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(List<Settings> settingsList) {
        this.settingsList = settingsList;
    }

    public Settings newSettings() {
        Settings aNew = new Settings(generateId(), "new");
        settingsList.add(aNew);
        return aNew;
    }

    public Settings copySettings(Settings settings) {
        Settings newSettings = new Settings();
        XmlSerializerUtil.copyBean(settings, newSettings);
        newSettings.setName(settings.getName() + " copy");
        newSettings.setId(generateId());
        settingsList.add(newSettings);
        return settings;
    }


    public void saveSettings(Settings settings, Project project) {
        if (settings.getId() == null) {
            addToGlobalSettings(settings, project);
        } else {
            for (Settings settings1 : settingsList) {
                if (settings1.getId().equals(settings.getId())) {
                    XmlSerializerUtil.copyBean(settings, settings1);
                }
            }
        }
    }

    private void addToGlobalSettings(Settings newSettings, Project project) {
        newSettings.setId(lastId + generateId());
        if (newSettings.getName() == null) {
            if (newSettings.equalsContent(new Settings())) {
                newSettings.setName("default");
            } else {
                newSettings.setName(project.getName());
            }
        }
        settingsList.add(newSettings);
    }

    private int generateId() {
        Integer newId = ++lastId;
        for (Settings settings : settingsList) {
            if (settings.getId().equals(newId)) {
                newId = generateId();
            }
        }
        return newId;
    }

    @NotNull
    public Settings getSettings(@NotNull Settings state, @NotNull Project project) {
        if (state.getId() == null) {
            Settings duplicateSettings = getDuplicateSettings(state);
            if (duplicateSettings != null) {
                return duplicateSettings;
            }
            addToGlobalSettings(state, project);
            return state;
        } else {
            for (Settings settings : settingsList) {
                if (settings.getId().equals(state.getId())) {
                    return settings;
                }
            }
            addToGlobalSettings(state, project);
            return state;
        }
    }

    @Nullable
    private Settings getDuplicateSettings(Settings state) {
        for (Settings settings : settingsList) {
            if (settings.equalsContent(state)) {
                return settings;
            }
        }
        return null;
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "EclipseCodeFormatterGlobalSettings";
    }

    @NotNull
    @Override
    public File[] getExportFiles() {
        return new File[]{PathManager.getOptionsFile("eclipseCodeFormatter")};
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Eclipse Code Formatter";
    }
}
