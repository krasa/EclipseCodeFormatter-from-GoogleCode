/*
 * External Code Formatter Copyright (c) 2007-2009 Esko Luontola, www.orfjackal.net Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package krasa.formatter.settings;

import krasa.formatter.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Esko Luontola
 * @author Vojtech Krasa
 * @since 4.12.2007
 */
public class Settings {
    public static final String LINE_SEPARATOR = "\n";


    private String name = null;
    private Long id = null;


    private String pathToConfigFileJS = "";
    private boolean enableJavaFormatting = true;
    private boolean enableJSFormatting = false;

    @NotNull
    private Formatter formatter = Formatter.DEFAULT;
    @NotNull
    private String pathToConfigFileJava = "";
    private String disabledFileTypes = "";
    private boolean optimizeImports = true;
    private boolean importOrderFromFile = false;
    private boolean formatOtherFileTypesWithIntelliJ = true;
    private boolean formatSeletedTextInAllFileTypes = true;
    private Integer notifyFromTextLenght = 300;
    private String importOrder = "java;javax;org;com;";
    private String importOrderConfigFilePath = "";
    private String selectedJavaProfile = null;
    private boolean defaultSettings = false;

    public Settings() {
    }

    public Settings(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public DisabledFileTypeSettings geDisabledFileTypeSettings() {
        return new DisabledFileTypeSettings(disabledFileTypes);
    }

    public String getSelectedJavaProfile() {
        return selectedJavaProfile;
    }

    public void setSelectedJavaProfile(String selectedJavaProfile) {
        this.selectedJavaProfile = selectedJavaProfile;
    }

    public String getPathToConfigFileJS() {
        return pathToConfigFileJS;
    }

    public void setPathToConfigFileJS(final String pathToConfigFileJS) {
        this.pathToConfigFileJS = pathToConfigFileJS;
    }

    public boolean isEnableJavaFormatting() {
        return enableJavaFormatting;
    }

    public void setEnableJavaFormatting(final boolean enableJavaFormatting) {
        this.enableJavaFormatting = enableJavaFormatting;
    }

    public boolean isEnableJSFormatting() {
        return enableJSFormatting;
    }

    public void setEnableJSFormatting(final boolean enableJSFormatting) {
        this.enableJSFormatting = enableJSFormatting;
    }

    public List<String> getImportOrderAsList() {
        return StringUtils.trimToList(importOrder);
    }

    public String getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(final String importOrder) {
        this.importOrder = importOrder;
    }

    public String getImportOrderConfigFilePath() {
        return importOrderConfigFilePath;
    }

    public void setImportOrderConfigFilePath(final String importOrderConfigFilePath) {
        this.importOrderConfigFilePath = importOrderConfigFilePath;
    }

    public boolean isDefaultSettings() {
        return defaultSettings;
    }


    public static enum Formatter {
        DEFAULT,
        ECLIPSE
    }

    public static enum Location {
        PROJECT,
        APPLICATION
    }

    public boolean isFormatSeletedTextInAllFileTypes() {
        return formatSeletedTextInAllFileTypes;
    }

    public void setFormatSeletedTextInAllFileTypes(boolean formatSeletedTextInAllFileTypes) {
        this.formatSeletedTextInAllFileTypes = formatSeletedTextInAllFileTypes;
    }

    public boolean isFormatOtherFileTypesWithIntelliJ() {
        return formatOtherFileTypesWithIntelliJ;
    }

    public void setFormatOtherFileTypesWithIntelliJ(boolean formatOtherFileTypesWithIntelliJ) {
        this.formatOtherFileTypesWithIntelliJ = formatOtherFileTypesWithIntelliJ;
    }

    public Integer getNotifyFromTextLenght() {
        return notifyFromTextLenght;
    }

    public void setNotifyFromTextLenght(Integer notifyFromTextLenght) {
        this.notifyFromTextLenght = notifyFromTextLenght;
    }

    public boolean isOptimizeImports() {
        return optimizeImports;
    }

    public String getDisabledFileTypes() {
        return disabledFileTypes;
    }

    public void setDisabledFileTypes(String disabledFileTypes) {
        this.disabledFileTypes = disabledFileTypes;
    }

    public void setOptimizeImports(boolean optimizeImports) {
        this.optimizeImports = optimizeImports;
    }

    @NotNull
    public Formatter getFormatter() {
        return formatter;
    }

    public void setFormatter(@NotNull Formatter formatter) {
        this.formatter = formatter;
    }

    @NotNull
    public String getPathToConfigFileJava() {
        return pathToConfigFileJava;
    }

    public void setPathToConfigFileJava(@NotNull String pathToConfigFileJava) {
        this.pathToConfigFileJava = pathToConfigFileJava;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean equalsContent(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Settings settings = (Settings) o;

        if (enableJSFormatting != settings.enableJSFormatting) return false;
        if (enableJavaFormatting != settings.enableJavaFormatting) return false;
        if (formatOtherFileTypesWithIntelliJ != settings.formatOtherFileTypesWithIntelliJ) return false;
        if (formatSeletedTextInAllFileTypes != settings.formatSeletedTextInAllFileTypes) return false;
        if (importOrderFromFile != settings.importOrderFromFile) return false;
        if (optimizeImports != settings.optimizeImports) return false;
        if (disabledFileTypes != null ? !disabledFileTypes.equals(settings.disabledFileTypes) : settings.disabledFileTypes != null)
            return false;
        if (formatter != settings.formatter) return false;
        if (importOrder != null ? !importOrder.equals(settings.importOrder) : settings.importOrder != null)
            return false;
        if (importOrderConfigFilePath != null ? !importOrderConfigFilePath.equals(settings.importOrderConfigFilePath) : settings.importOrderConfigFilePath != null)
            return false;
        if (notifyFromTextLenght != null ? !notifyFromTextLenght.equals(settings.notifyFromTextLenght) : settings.notifyFromTextLenght != null)
            return false;
        if (pathToConfigFileJS != null ? !pathToConfigFileJS.equals(settings.pathToConfigFileJS) : settings.pathToConfigFileJS != null)
            return false;
        if (!pathToConfigFileJava.equals(settings.pathToConfigFileJava)) return false;

        return true;
    }

    public boolean isImportOrderFromFile() {
        return importOrderFromFile;
    }

    public void setDefaultSettings(boolean defaultSettings) {
        this.defaultSettings = defaultSettings;
    }

    public void setImportOrderFromFile(boolean importOrderFromFile) {
        this.importOrderFromFile = importOrderFromFile;


    }
}
