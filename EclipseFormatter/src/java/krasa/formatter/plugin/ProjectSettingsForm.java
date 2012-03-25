/*
 * External Code Formatter Copyright (c) 2007-2009 Esko Luontola, www.orfjackal.net Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package krasa.formatter.plugin;

import com.intellij.ide.ui.ListCellRendererWrapper;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SortedComboBoxModel;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.ui.popup.list.ListPopupImpl;
import com.intellij.ui.popup.mock.MockConfirmation;
import krasa.formatter.settings.GlobalSettings;
import krasa.formatter.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Configuration dialog for changing the {@link krasa.formatter.settings.Settings} of the plugin.
 *
 * @author Esko Luontola
 * @author Vojtech Krasa
 * @since 4.12.2007
 */
public class ProjectSettingsForm {
    private static final Logger LOG = Logger.getInstance(ProjectSettingsForm.class.getName());

    private static final Color NORMAL = new JTextField().getBackground();
    private static final Color WARNING = new Color(255, 255, 204);
    private static final Color ERROR = new Color(255, 204, 204);

    private JPanel rootComponent;

    private JRadioButton useDefaultFormatter;
    private JRadioButton useEclipseFormatter;

    private JLabel eclipseSupportedFileTypesLabel;

    private JTextField disabledFileTypes;
    private JLabel disabledFileTypesHelpLabel;
    private JRadioButton doNotFormatOtherFilesRadioButton;
    private JRadioButton formatOtherFilesWithExceptionsRadioButton;
    private JCheckBox formatSelectedTextInAllFileTypes;

    private JLabel eclipsePreferenceFileJavaLabel;
    private JLabel eclipsePreferenceFileJSLabel;

    private JTextField pathToEclipsePreferenceFileJava;
    private JTextField pathToEclipsePreferenceFileJS;

    private JLabel eclipsePrefsExample;
    private JLabel eclipsePrefsExampleJS;

    private JCheckBox enableJavaFormatting;
    private JCheckBox enableJSFormatting;

    private JButton eclipsePreferenceFilePathJavaBrowse;
    private JButton eclipsePreferenceFilePathJSBrowse;


    private JCheckBox optimizeImportsCheckBox;
    private JLabel importOrderLabel;
    private JFormattedTextField importOrder;
    private JLabel importOrderManualExample;
    private JTextField pathToImportOrderPreferenceFile;
    private JButton pathToImportOrderPreferenceFileBrowse;
    private JLabel importOrderPreferenceFileExample;
    private JRadioButton importOrderConfigurationFromFileRadioButton;
    private JRadioButton importOrderConfigurationManualRadioButton;

    private JComboBox profiles;
    private JButton newProfile;
    private JButton copyProfile;
    private JButton rename;
    private JButton delete;
    private Settings displayedSettings;
    private Settings projectSettings;
    /**
     * do not delete
     */
    private JTextArea help;

    private final List<Popup> visiblePopups = new ArrayList<Popup>();
    @NotNull
    private Project project;
    protected SortedComboBoxModel<Settings> profilesModel;

    private void updateComponents() {
        hidePopups();

        enabledBy(new JComponent[]{eclipseSupportedFileTypesLabel, enableJavaFormatting, enableJSFormatting,
                doNotFormatOtherFilesRadioButton, formatOtherFilesWithExceptionsRadioButton, importOrderPreferenceFileExample, importOrderConfigurationFromFileRadioButton, importOrderConfigurationManualRadioButton,
                formatSelectedTextInAllFileTypes,}, useEclipseFormatter);

        enabledBy(new JComponent[]{pathToEclipsePreferenceFileJava, eclipsePrefsExample,
                eclipsePreferenceFileJavaLabel, optimizeImportsCheckBox, eclipsePreferenceFilePathJavaBrowse},
                useEclipseFormatter, enableJavaFormatting);

        enabledBy(new JComponent[]{importOrder, pathToImportOrderPreferenceFile, pathToImportOrderPreferenceFileBrowse, importOrderManualExample, importOrderLabel, importOrderPreferenceFileExample, importOrderConfigurationFromFileRadioButton, importOrderConfigurationManualRadioButton
        },
                optimizeImportsCheckBox);

        enabledBy(new JComponent[]{pathToImportOrderPreferenceFile, importOrderPreferenceFileExample, pathToImportOrderPreferenceFileBrowse
        },
                importOrderConfigurationFromFileRadioButton);

        enabledBy(new JComponent[]{importOrder, importOrderManualExample,
        },
                importOrderConfigurationManualRadioButton);

        enabledBy(new JComponent[]{pathToEclipsePreferenceFileJS, eclipsePrefsExampleJS, eclipsePreferenceFileJSLabel,
                eclipsePreferenceFilePathJSBrowse}, enableJSFormatting);

        enabledBy(new JComponent[]{disabledFileTypes, disabledFileTypesHelpLabel,},
                formatOtherFilesWithExceptionsRadioButton);

    }


    public ProjectSettingsForm(Project project) {
        this.project = project;
        JToggleButton[] modifiableButtons = new JToggleButton[]{useDefaultFormatter, useEclipseFormatter,
                optimizeImportsCheckBox, enableJavaFormatting, doNotFormatOtherFilesRadioButton,
                formatOtherFilesWithExceptionsRadioButton, formatSelectedTextInAllFileTypes, enableJSFormatting, importOrderConfigurationManualRadioButton, importOrderConfigurationFromFileRadioButton
                ,};
        for (JToggleButton button : modifiableButtons) {
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateComponents();
                }
            });
        }

        JTextField[] modifiableFields = new JTextField[]{pathToEclipsePreferenceFileJava, pathToEclipsePreferenceFileJS,
                disabledFileTypes, importOrder, pathToImportOrderPreferenceFile};
        for (JTextField field : modifiableFields) {
            field.getDocument().addDocumentListener(new DocumentAdapter() {
                protected void textChanged(DocumentEvent e) {
                    updateComponents();
                }
            });
        }

        eclipsePreferenceFilePathJavaBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseForFile(pathToEclipsePreferenceFileJava);
            }
        });
        pathToImportOrderPreferenceFileBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseForFile(pathToImportOrderPreferenceFile);
            }
        });
        eclipsePreferenceFilePathJSBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseForFile(pathToEclipsePreferenceFileJS);
            }
        });

        rootComponent.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                // Called when component becomes visible, to ensure that the
                // popups
                // are visible when the form is shown for the first time.
                updateComponents();
            }

            public void ancestorRemoved(AncestorEvent event) {
            }

            public void ancestorMoved(AncestorEvent event) {
            }
        });
        newProfile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log("newProfile action");
                if (isModified(displayedSettings)) {
                    createConfirmation("Profile was modified, save changes to current profile?", "Yes", "No", new Runnable() {
                                @Override
                                public void run() {
                                    exportDisplayedSettings();
                                    createProfile();
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    importFrom(displayedSettings);
                                    createProfile();
                                }
                            }, 0
                    ).showInFocusCenter();
                } else {
                    createProfile();
                }
            }

            private void createProfile() {
                log("createProfile");
                Settings settings = GlobalSettings.getInstance().newSettings();
                refreshProfilesModel();
                profiles.setSelectedItem(settings);
            }
        });
        copyProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("copyProfile action");
                if (isModified(displayedSettings)) {
                    ListPopup confirmation = createConfirmation("Profile was modified, save changes to current profile?", "Yes", "No", new Runnable() {
                                @Override
                                public void run() {
                                    exportDisplayedSettings();
                                    copyProfile();
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                    importFrom(displayedSettings);
                                    copyProfile();
                                }
                            }, 0
                    );

                    confirmation.showInFocusCenter();
                } else {
                    copyProfile();
                }
            }

            private void copyProfile() {
                log("copyProfile");
                Settings settings = GlobalSettings.getInstance().copySettings(displayedSettings);
                refreshProfilesModel();
                profiles.setSelectedItem(settings);

            }
        });
        profilesModel = createProfilesModel();
        profiles.setModel(profilesModel);
        profiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("profiles ActionListener");
//                && isSameId()
                if (displayedSettings != null && getSelectedItem() != null && isModified(displayedSettings)) {
                    log("profiles ActionListener showConfirmationDialogOnProfileChange");
                    showConfirmationDialogOnProfileChange();
                } else if (displayedSettings != null && getSelectedItem() != null) {
                    log("profiles ActionListener importFrom");
                    importFromInternal(getSelectedItem());
                }
            }


        });


        profiles.setRenderer(new ListCellRendererWrapper(profiles.getRenderer()) {
            @Override
            public void customize(JList jList, Object value, int i, boolean b, boolean b1) {
                if (value != null) {
                    setText(((Settings) value).getName());
                }
            }
        });
        rename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("rename action");
                final JTextField content = new JTextField();
                content.setText(displayedSettings.getName());
                JBPopup balloon = PopupFactoryImpl.getInstance().createComponentPopupBuilder(content, content).createPopup();
                balloon.setMinimumSize(new Dimension(200, 20));
                balloon.addListener(new JBPopupListener() {
                    @Override
                    public void beforeShown(LightweightWindowEvent event) {
                    }

                    @Override
                    public void onClosed(LightweightWindowEvent event) {
                        displayedSettings.setName(content.getText());
                    }
                });
                balloon.showUnderneathOf(rename);
            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("delete action");
                int selectedIndex = profiles.getSelectedIndex();
                GlobalSettings.getInstance().delete(getSelectedItem(), getProject());
                profiles.setModel(profilesModel = createProfilesModel());
                int itemCount = profiles.getItemCount();
                if (selectedIndex < itemCount && selectedIndex >= 0) {
                    Object itemAt = profiles.getItemAt(selectedIndex);
                    importFromInternal((Settings) itemAt);
                    profiles.setSelectedIndex(selectedIndex);
                }
                if (selectedIndex == itemCount && selectedIndex - 1 >= 0) {
                    Object itemAt = profiles.getItemAt(selectedIndex - 1);
                    importFromInternal((Settings) itemAt);
                    profiles.setSelectedIndex(selectedIndex - 1);
                } else {
                    Settings defaultSettings = GlobalSettings.getInstance().getDefaultSettings();
                    importFromInternal(defaultSettings);
                    profiles.setSelectedItem(defaultSettings);
                }

            }
        });
    }

    private SortedComboBoxModel<Settings> createProfilesModel() {
        SortedComboBoxModel<Settings> settingsSortedComboBoxModel = new SortedComboBoxModel<Settings>(new Comparator<Settings>() {
            @Override
            public int compare(Settings o1, Settings o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        settingsSortedComboBoxModel.setAll(GlobalSettings.getInstance().getSettingsList());
        return settingsSortedComboBoxModel;
    }

    @NotNull
    private Project getProject() {
        return ProjectSettingsForm.this.project;
    }

    private void showConfirmationDialogOnProfileChange() {
        createConfirmation("Profile was modified, save changes?", "Yes", "No", new Runnable() {
                    @Override
                    public void run() {
                        exportDisplayedSettings();
                        importFrom(getSelectedItem());
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        importFromInternal(getSelectedItem());
                    }
                }, 0
        ).showInCenterOf(profiles);
    }

    private boolean isSameId() {
//        return !displayedSettings.getId().equals(getSelectedItem().getId());
        return displayedSettings.getId().equals(projectSettings.getId());
    }

    private void refreshProfilesModel() {
        profilesModel.setAll(GlobalSettings.getInstance().getSettingsList());
    }

    private Settings getSelectedItem() {
        Object selectedItem = profiles.getSelectedItem();
        return (Settings) selectedItem;
    }

    private void browseForFile(@NotNull final JTextField target) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
        descriptor.setHideIgnored(false);

        descriptor.setTitle("Select config file");
        String text = target.getText();
        final VirtualFile toSelect = text == null || text.isEmpty() ? getProject().getBaseDir()
                : LocalFileSystem.getInstance().findFileByPath(text);

        // 10.5 does not have #chooseFile
        VirtualFile[] virtualFile = FileChooser.chooseFiles(getProject(), descriptor, toSelect);
        if (virtualFile != null && virtualFile.length > 0) {
            target.setText(virtualFile[0].getPath());
        }

    }


    private void enabledBy(@NotNull JComponent[] targets, @NotNull JToggleButton... control) {
        boolean b = true;
        for (JToggleButton jToggleButton : control) {
            b = b && (jToggleButton.isEnabled() && jToggleButton.isSelected());
        }
        for (JComponent target : targets) {
            target.setEnabled(b);
        }
    }

    private void showPopup(@NotNull JComponent parent, @NotNull String message) {
        if (!parent.isShowing() || !parent.isEnabled()) {
            return; // if getLocationOnScreen is called when the component is
            // not showing, an exception is thrown
        }
        JToolTip tip = new JToolTip();
        tip.setTipText(message);
        Dimension tipSize = tip.getPreferredSize();

        Point location = parent.getLocationOnScreen();
        int x = (int) location.getX();
        int y = (int) (location.getY() - tipSize.getHeight());

        Popup popup = PopupFactory.getSharedInstance().getPopup(parent, tip, x, y);
        popup.show();
        visiblePopups.add(popup);
    }

    private void hidePopups() {
        for (Iterator<Popup> it = visiblePopups.iterator(); it.hasNext(); ) {
            Popup popup = it.next();
            popup.hide();
            it.remove();
        }
    }

    @NotNull
    public JPanel getRootComponent() {
        return rootComponent;
    }

    public void importFrom(@NotNull Settings in) {
        log("importFrom");
        boolean displayedSettingsIsNull = displayedSettings == null;
        if (displayedSettingsIsNull) {
            projectSettings = in;
        }
        //this needs to be before setting displayedSettings so we can disable action listener
        // and also when we import already displayed settings == reset, no notification is needed.
        if (displayedSettings == null || in != displayedSettings) {
            profiles.setSelectedItem(in);
        }

        importFromInternal(in);
    }

    /**
     * does not update profiles DropDown
     */
    private void importFromInternal(Settings in) {
        log("importFromInternal");
        displayedSettings = in;
        formatOtherFilesWithExceptionsRadioButton.setSelected(in.isFormatOtherFileTypesWithIntelliJ());
        doNotFormatOtherFilesRadioButton.setSelected(!in.isFormatOtherFileTypesWithIntelliJ());
        useDefaultFormatter.setSelected(in.getFormatter().equals(Settings.Formatter.DEFAULT));
        useEclipseFormatter.setSelected(in.getFormatter().equals(Settings.Formatter.ECLIPSE));
        importOrderConfigurationFromFileRadioButton.setSelected(in.isImportOrderFromFile());
        importOrderConfigurationManualRadioButton.setSelected(!in.isImportOrderFromFile());
        setData(in);
        updateComponents();
    }

    public void setData(Settings data) {
        optimizeImportsCheckBox.setSelected(data.isOptimizeImports());
        formatSelectedTextInAllFileTypes.setSelected(data.isFormatSeletedTextInAllFileTypes());
        pathToEclipsePreferenceFileJava.setText(data.getPathToConfigFileJava());
        pathToEclipsePreferenceFileJS.setText(data.getPathToConfigFileJS());
        disabledFileTypes.setText(data.getDisabledFileTypes());
        enableJSFormatting.setSelected(data.isEnableJSFormatting());
        enableJavaFormatting.setSelected(data.isEnableJavaFormatting());
        importOrder.setText(data.getImportOrder());
        pathToImportOrderPreferenceFile.setText(data.getImportOrderConfigFilePath());
    }

    public Settings exportDisplayedSettings() {
        log("exportTo");
        if (useEclipseFormatter.isSelected()) {
            displayedSettings.setFormatter(Settings.Formatter.ECLIPSE);
        } else {
            displayedSettings.setFormatter(Settings.Formatter.DEFAULT);
        }
        displayedSettings.setFormatOtherFileTypesWithIntelliJ(formatOtherFilesWithExceptionsRadioButton.isSelected());
        displayedSettings.setImportOrderFromFile(importOrderConfigurationFromFileRadioButton.isSelected());
        getData(displayedSettings);
        return displayedSettings;
    }

    public void getData(Settings data) {
        data.setOptimizeImports(optimizeImportsCheckBox.isSelected());
        data.setFormatSeletedTextInAllFileTypes(formatSelectedTextInAllFileTypes.isSelected());
        data.setPathToConfigFileJava(pathToEclipsePreferenceFileJava.getText());
        data.setPathToConfigFileJS(pathToEclipsePreferenceFileJS.getText());
        data.setDisabledFileTypes(disabledFileTypes.getText());
        data.setEnableJSFormatting(enableJSFormatting.isSelected());
        data.setEnableJavaFormatting(enableJavaFormatting.isSelected());
        data.setImportOrder(importOrder.getText());
        data.setImportOrderConfigFilePath(pathToImportOrderPreferenceFile.getText());
    }

    public boolean isModified(Settings data) {
        if (customIsModified(data)) {
            return true;
        }
        if (optimizeImportsCheckBox.isSelected() != data.isOptimizeImports()) return true;
        if (formatSelectedTextInAllFileTypes.isSelected() != data.isFormatSeletedTextInAllFileTypes()) return true;
        if (pathToEclipsePreferenceFileJava.getText() != null ? !pathToEclipsePreferenceFileJava.getText().equals(data.getPathToConfigFileJava()) : data.getPathToConfigFileJava() != null)
            return true;
        if (pathToEclipsePreferenceFileJS.getText() != null ? !pathToEclipsePreferenceFileJS.getText().equals(data.getPathToConfigFileJS()) : data.getPathToConfigFileJS() != null)
            return true;
        if (disabledFileTypes.getText() != null ? !disabledFileTypes.getText().equals(data.getDisabledFileTypes()) : data.getDisabledFileTypes() != null)
            return true;
        if (enableJSFormatting.isSelected() != data.isEnableJSFormatting()) return true;
        if (enableJavaFormatting.isSelected() != data.isEnableJavaFormatting()) return true;
        if (importOrder.getText() != null ? !importOrder.getText().equals(data.getImportOrder()) : data.getImportOrder() != null)
            return true;
        if (pathToImportOrderPreferenceFile.getText() != null ? !pathToImportOrderPreferenceFile.getText().equals(data.getImportOrderConfigFilePath()) : data.getImportOrderConfigFilePath() != null)
            return true;
        return false;
    }

    private boolean customIsModified(Settings data) {
        if (useDefaultFormatter.isSelected() != data.getFormatter().equals(Settings.Formatter.DEFAULT)) {
            return true;
        }
        if (useEclipseFormatter.isSelected() != data.getFormatter().equals(Settings.Formatter.ECLIPSE)) {
            return true;
        }
        if (formatOtherFilesWithExceptionsRadioButton.isSelected() != data.isFormatOtherFileTypesWithIntelliJ()) {
            return true;
        }
        if (doNotFormatOtherFilesRadioButton.isSelected() != !data.isFormatOtherFileTypesWithIntelliJ()) {
            return true;
        }
        if (importOrderConfigurationFromFileRadioButton.isSelected() != data.isImportOrderFromFile()) {
            return true;
        }
        return false;
    }

    public Settings getDisplayedSettings() {
        return displayedSettings;
    }


    public ListPopup createConfirmation(String title, final String yesText, String noText, final Runnable onYes, final Runnable onNo, int defaultOptionIndex) {

        final BaseListPopupStep<String> step = new BaseListPopupStep<String>(title, new String[]{yesText, noText}) {
            public PopupStep onChosen(String selectedValue, final boolean finalChoice) {
                if (selectedValue.equals(yesText)) {
                    onYes.run();
                } else {
                    onNo.run();
                }
                return FINAL_CHOICE;
            }

            public void canceled() {
            }

            public boolean isMnemonicsNavigationEnabled() {
                return true;
            }
        };
        step.setDefaultOptionIndex(defaultOptionIndex);

        final ApplicationEx app = ApplicationManagerEx.getApplicationEx();
        return app == null || !app.isUnitTestMode() ? new ListPopupImpl(step) : new MockConfirmation(step, yesText);
    }


    private void log(String message) {
        System.err.println(message);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
