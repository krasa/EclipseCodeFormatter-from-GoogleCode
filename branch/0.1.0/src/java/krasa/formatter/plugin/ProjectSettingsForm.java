/*
 * External Code Formatter
 * Copyright (c) 2007-2009  Esko Luontola, www.orfjackal.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package krasa.formatter.plugin;

import com.intellij.ui.DocumentAdapter;
import krasa.formatter.Messages;
import krasa.formatter.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Configuration dialog for changing the {@link krasa.formatter.settings.Settings} of the plugin.
 *
 * @author Esko Luontola
 * @since 4.12.2007
 */
public class ProjectSettingsForm {

	private static final Color NORMAL = new JTextField().getBackground();
	private static final Color WARNING = new Color(255, 255, 204);
	private static final Color ERROR = new Color(255, 204, 204);

	private JPanel rootComponent;

	private JRadioButton useDefaultFormatter;
	private JRadioButton useEclipseFormatter;

	private JTextField eclipseSupportedFileTypes;
	private JLabel eclipseSupportedFileTypesLabel;
	private JTextField eclipsePrefs;
	private JButton eclipsePrefsBrowse;
	private JLabel eclipsePrefsLabel;
	private JTextPane eclipsePrefsExample;
	private JTextArea eclipseFormatterCannotFormatTextArea;


	private final List<Popup> visiblePopups = new ArrayList<Popup>();
	@Nullable
	private File lastDirectory;

	public ProjectSettingsForm() {
		JToggleButton[] modifyableButtons = new JToggleButton[]{
				useDefaultFormatter,
				useEclipseFormatter,
		};
		for (JToggleButton button : modifyableButtons) {
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateComponents();
				}
			});
		}

		JTextField[] modifyableFields = new JTextField[]{
				eclipsePrefs,
		};
		for (JTextField field : modifyableFields) {
			field.getDocument().addDocumentListener(new DocumentAdapter() {
				protected void textChanged(DocumentEvent e) {
					updateComponents();
				}
			});
		}

		eclipseSupportedFileTypes.setText("*.java");
		eclipsePrefsBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseForFile(eclipsePrefs);
			}
		});

		rootComponent.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				// Called when component becomes visible, to ensure that the popups
				// are visible when the form is shown for the first time.
				updateComponents();
			}

			public void ancestorRemoved(AncestorEvent event) {
			}

			public void ancestorMoved(AncestorEvent event) {
			}
		});
	}

	private void browseForFile(@NotNull JTextField target) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileHidingEnabled(false);	// Eclipse's prefs file is in a hidden ".settings" directory

		if (target.getText().equals("") && lastDirectory != null) {
			chooser.setCurrentDirectory(lastDirectory);
		} else {
			File currentSelection = new File(target.getText());
			chooser.setCurrentDirectory(currentSelection);
			chooser.setSelectedFile(currentSelection);
		}

		int result = chooser.showOpenDialog(rootComponent);
		if (result == JFileChooser.APPROVE_OPTION) {
			target.setText(chooser.getSelectedFile().getAbsolutePath());
		}
		lastDirectory = chooser.getCurrentDirectory();
	}

	private void updateComponents() {
		hidePopups();

		enabledBy(useEclipseFormatter, new JComponent[]{
				eclipseSupportedFileTypesLabel,
				eclipsePrefs,
				eclipsePrefsBrowse,
				eclipsePrefsLabel,
				eclipsePrefsExample,
		});
		if ( notEmpty(eclipsePrefs) && fileExists(eclipsePrefs)) {
			ok(eclipsePrefs);
		}

	}

	private void enabledBy(@NotNull JToggleButton control, @NotNull JComponent[] targets) {
		for (JComponent target : targets) {
			target.setEnabled(control.isEnabled() && control.isSelected());
		}
	}

	private boolean notEmpty(@NotNull JTextField field) {
		if (field.getText().trim().length() == 0) {
			field.setBackground(WARNING);
			showPopup(field, Messages.message( "warning.requiredField" ));
			return false;
		}
		return true;
	}

	private boolean containsText(@NotNull String needle, @NotNull JTextField field) {
		if (!field.getText().contains(needle)) {
			field.setBackground(ERROR);
			showPopup(field, Messages.message("warning.mustContain", needle));
			return false;
		}
		return true;
	}

	private boolean fileExists(@NotNull JTextField field) {
		if (!new File(field.getText()).isFile()) {
			field.setBackground(ERROR);
			showPopup(field, Messages.message("warning.noSuchFile"));
			return false;
		}
		return true;
	}

	private void atLeastOneSelected(JToggleButton... buttons) {
		for (JToggleButton button : buttons) {
			if (button.isSelected()) {
				return;
			}
		}
		showPopup(buttons[0], Messages.message("warning.selectAtLeastOne"));
	}

	private void ok(@NotNull JTextField field) {
		field.setBackground(NORMAL);
	}

	private void showPopup(@NotNull JComponent parent, @NotNull String message) {
		if (!parent.isShowing() || !parent.isEnabled()) {
			return; // if getLocationOnScreen is called when the component is not showing, an exception is thrown
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
		useDefaultFormatter.setSelected(in.getFormatter().equals( Settings.Formatter.DEFAULT));
		useEclipseFormatter.setSelected(in.getFormatter().equals( Settings.Formatter.ECLIPSE));
		eclipsePrefs.setText(in.getEclipsePrefs());
		updateComponents();
	}

	public void exportTo(@NotNull Settings out) {
		if (useEclipseFormatter.isSelected()) {
			out.setFormatter( Settings.Formatter.ECLIPSE);
		} else {
			out.setFormatter( Settings.Formatter.DEFAULT);
		}

		out.setEclipsePrefs(eclipsePrefs.getText());
	}

	@SuppressWarnings({"RedundantIfStatement", "ConstantConditions"})
	public boolean isModified(Settings data) {
		if (useDefaultFormatter.isSelected() != data.getFormatter().equals( Settings.Formatter.DEFAULT)) {
			return true;
		}
		if (useEclipseFormatter.isSelected() != data.getFormatter().equals( Settings.Formatter.ECLIPSE)) {
			return true;
		}
		if (eclipsePrefs.getText() != null ? !eclipsePrefs.getText().equals(data.getEclipsePrefs()) : data.getEclipsePrefs() != null) {
			return true;
		}

		return false;
	}
}
