package krasa.formatter.settings;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.impl.ProjectManagerImpl;
import com.intellij.util.xmlb.XmlSerializerUtil;
import krasa.formatter.plugin.Notifier;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
public class GlobalSettings implements ApplicationComponent, PersistentStateComponent<GlobalSettings>, ExportableApplicationComponent {
    private List<Settings> settingsList = new ArrayList<Settings>();
    private List<Long> deletedSettingsId = new ArrayList<Long>();

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
        newSettings.setDefaultSettings(false);
        settingsList.add(newSettings);
        return newSettings;
    }


    public void updateSettings(Settings settings, Project project) {
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
        newSettings.setId(generateId());
        if (newSettings.getName() == null) {
            newSettings.setName(project.getName());
        }
        settingsList.add(newSettings);
    }

    private Long generateId() {
        long newId = new Date().getTime();
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
//            Settings duplicateSettings = getDuplicateSettings(state);
            if (isSameAsDefault(state)) {
                return getDefaultSettings();
            }
            addToGlobalSettings(state, project);
            return state;
        } else {
            for (Settings settings : settingsList) {
                if (settings.getId().equals(state.getId()) || settings.getName().equals(state.getName())) {
                    return settings;
                }
            }
            if (deletedSettingsId.contains(state.getId())) {
                Settings defaultSettings = getDefaultSettings();
                Notifier.notifyDeletedSettings(project);
                return defaultSettings;
            }
            addToGlobalSettings(state, project);
            return state;
        }
    }

    private boolean isSameAsDefault(Settings state) {
        return getDefaultSettings().equalsContent(state);
    }

    public Settings getDefaultSettings() {
        for (Settings settings : settingsList) {
            if (settings.isDefault()) {
                return settings;
            }
        }
        Settings aDefault = createDefaultSettings();
        settingsList.add(aDefault);
        return aDefault;
    }

    private Settings createDefaultSettings() {
        Settings aDefault = new Settings(generateId(), "default");
        aDefault.setDefaultSettings(true);
        return aDefault;
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

    public void delete(Settings settings, Project project) {
        settingsList.remove(settings);
        deletedSettingsId.add(settings.getId());
        getDefaultSettings(); //to create default setting when it was deleted
        notifyProjectsWhichUsesThisSettings(settings, project);

    }

    private void notifyProjectsWhichUsesThisSettings(Settings deletedSettings, Project project) {
        Project[] openProjects = ProjectManagerImpl.getInstance().getOpenProjects();
        for (Project openProject : openProjects) {
            ProjectSettingsComponent component = openProject.getComponent(ProjectSettingsComponent.class);
            if (component != null) {
                Settings state = component.getSettings();
                if (deletedSettings.getId().equals(state.getId())) {
                    component.loadState(getDefaultSettings());
                    if (project != openProject) {
                        Notifier.notifyDeletedSettings(component.getProject());
                    }
                }
            }
        }
    }


    public Settings loadState(Settings state, ProjectSettingsComponent projectSettingsComponent) {
        return getSettings(state, projectSettingsComponent.getProject());
    }


}
