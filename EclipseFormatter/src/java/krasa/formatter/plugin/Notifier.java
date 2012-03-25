package krasa.formatter.plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import krasa.formatter.settings.ProjectSettingsComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vojtech Krasa
 */
public class Notifier {

    public static final String FORMATTING_FAILED_PROBABLY_DUE_TO_NOT_COMPILABLE_CODE_OR_WRONG_CONFIG_FILE = "formatting failed, probably due to not compilable code or wrong config file";
    public static final String NO_FILE_TO_FORMAT = "No file to format";

    @NotNull
    private final Project project;

    public Notifier(Project project) {
        this.project = project;
    }

    public void notifyFailedFormatting(PsiFile psiFile, boolean formattedByIntelliJ, Exception e) {
        String error = e.getMessage() == null ? "" : e.getMessage();
        String content;
        if (!formattedByIntelliJ) {
            content = psiFile.getName() + " failed to format with Eclipse code formatter.\n" + error;
        } else {
            content = psiFile.getName() + " failed to format with IntelliJ code formatter.\n" + error;
        }
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID_ERROR, "", content, NotificationType.ERROR);
        showNotification(notification);
    }

    void notifyFormattingWasDisabled(PsiFile psiFile) {
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID_INFO, "", psiFile.getName()
                + " - formatting was disabled for this file type", NotificationType.WARNING);
        showNotification(notification);
    }

    void notifySuccessFormatting(PsiFile psiFile, boolean formattedByIntelliJ) {
        String content;
        if (formattedByIntelliJ) {
            content = psiFile.getName() + " formatted sucessfully by IntelliJ code formatter";
        } else {
            content = psiFile.getName() + " formatted sucessfully by Eclipse code formatter";
        }
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID_INFO, "", content, NotificationType.INFORMATION);
        showNotification(notification);
    }

    void showNotification(final Notification notification) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Notifications.Bus.notify(notification, project);
            }
        });
    }

    public void notifyBrokenImportSorter() {
        String content = "Formatting failed due to new Import optimizer.";
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID_ERROR, "", content, NotificationType.ERROR);
        showNotification(notification);

    }

    public static void notifyDeletedSettings(final Project project) {
        String content = "Eclipse formatter settings profile was deleted for project " + project.getName() +
                ". Default settings is used now.";
        final Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID_ERROR, "", content, NotificationType.ERROR);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Notifications.Bus.notify(notification, project);
            }
        });

    }

}
