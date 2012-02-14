package krasa.formatter;

import com.intellij.ide.actions.QuickSwitchSchemeAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import krasa.formatter.plugin.ProjectSettingsComponent;
import krasa.formatter.settings.Settings;

/**
 * @author Vojtech Krasa
 */
public class QuickChangeCodeFormatterAction extends QuickSwitchSchemeAction {

    protected void fillActions(final Project project, DefaultActionGroup group,
                               DataContext dataContext) {
        Settings.Formatter formatter = ProjectSettingsComponent
                .getInstance(project).getState().getFormatter();
        for (final Settings.Formatter lf : Settings.Formatter.values()) {
            group.add(new DumbAwareAction(lf.name(), "", lf == formatter
                    ? ourCurrentAction
                    : ourNotCurrentAction) {
                public void actionPerformed(AnActionEvent e) {
                    changeFormatter(project, lf);
                }
            });
        }
    }

    private void changeFormatter(Project project, Settings.Formatter lf) {
        ProjectSettingsComponent instance = ProjectSettingsComponent
                .getInstance(project);
        final Settings state = instance.getState();
        state.setFormatter(lf);
        instance.loadState(state);
    }

    protected boolean isEnabled() {
        return true;
    }
}
