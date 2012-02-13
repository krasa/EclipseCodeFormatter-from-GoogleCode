package krasa.formatter.plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vojtech Krasa
 */
public class Notifier {

    @NotNull
    private final Project project;

    public Notifier(Project project) {
        this.project = project;

    }

    public void notifyFailedFormatting(PsiFile psiFile, boolean formattedByIntelliJ, Exception e) {
        String error = e.getMessage() == null ? "" : e.getMessage();
        String content;
        if (!formattedByIntelliJ) {
            content = psiFile.getName() + " failed to format with Eclipse code formatter. " + error;
        } else {
            content = psiFile.getName() + " failed to format with IntelliJ code formatter. " + error;
        }
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID, "", content, NotificationType.ERROR);
        showNotification(notification);
    }

    void notifyFormattingWasDisabled(PsiFile psiFile) {
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID, "",
                psiFile.getName() + " - formatting was disabled for this file type", NotificationType.WARNING);
        showNotification(notification);
    }


    void notifySuccessFormatting(PsiFile psiFile, boolean formattedByIntelliJ) {
        String content;
        if (formattedByIntelliJ) {
            content = psiFile.getName() + " formatted sucessfully by IntelliJ code formatter";
        } else {
            content = psiFile.getName() + " formatted sucessfully by Eclipse code formatter";
        }
        Notification notification = new Notification(ProjectSettingsComponent.GROUP_DISPLAY_ID, "", content, NotificationType.INFORMATION);
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

}
