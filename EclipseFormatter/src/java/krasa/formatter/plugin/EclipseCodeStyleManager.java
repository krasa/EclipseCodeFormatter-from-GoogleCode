package krasa.formatter.plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.IncorrectOperationException;
import krasa.formatter.eclipse.InvalidPathToConfigFileException;
import krasa.formatter.settings.DisabledFileTypeSettings;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Supported operations are handled by Eclipse formatter, other by IntelliJ
 * formatter.
 * <p/>
 * TODO proper write action thread handling
 *
 * @author Vojtech Krasa
 * @since 30.10.20011
 */
public class EclipseCodeStyleManager extends DelegatingCodeStyleManager {

    private static final Logger LOG = Logger.getInstance(EclipseCodeStyleManager.class.getName());

    @NotNull
    private Settings settings;
    @NotNull
    private Notifier notifier;
    @NotNull
    private EclipseCodeFormatter eclipseCodeFormatter;

    public EclipseCodeStyleManager(@NotNull CodeStyleManager original,
                                   @NotNull Settings settings, @NotNull Project project) {
        super(original);
        this.settings = settings;

        notifier = new Notifier(project);
        eclipseCodeFormatter = new EclipseCodeFormatter(settings, project, original);
    }

    public void reformatText(@NotNull PsiFile psiFile, final int startOffset,
                             final int endOffset) throws IncorrectOperationException {
        boolean formattedByIntelliJ = false;
        try {
            ApplicationManager.getApplication().assertWriteAccessAllowed();
            PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
            CheckUtil.checkWritable(psiFile);

            if (psiFile.getVirtualFile() == null) {
                Notification notification = new Notification(
                        ProjectSettingsComponent.GROUP_DISPLAY_ID, "",
                        Notifier.NO_FILE_TO_FORMAT, NotificationType.ERROR);
                notifier.showNotification(notification);
                return;
            }

            boolean wholeFileOrSelectedText = isWholeFileOrSelectedText(psiFile, startOffset, endOffset);
            if (canReformatWithEclipse(psiFile) && wholeFileOrSelectedText) {
                eclipseCodeFormatter.format(psiFile, startOffset, endOffset);
                boolean skipSuccessFormattingNotification = shouldSkipNotification(startOffset,
                        endOffset, psiFile.getText());
                if (!skipSuccessFormattingNotification) {
                    notifier.notifySuccessFormatting(psiFile, false);
                }
            } else {
                if (shouldSkipFormatting(psiFile, startOffset, endOffset)) {
                    notifier.notifyFormattingWasDisabled(psiFile);
                } else {
                    formatWithIntelliJ(psiFile, startOffset, endOffset);
                    if (wholeFileOrSelectedText) {
                        notifier.notifySuccessFormatting(psiFile, true);
                    }
                }
            }


        } catch (final InvalidPathToConfigFileException e) {
            e.printStackTrace();
            LOG.debug(e);
            notifier.notify(e);
        } catch (final Exception e) {
            e.printStackTrace();
            LOG.error("startOffset" + startOffset + ", endOffset:" + endOffset + ", length of file " + psiFile.getText().length(), e);
            notifier.notifyFailedFormatting(psiFile, formattedByIntelliJ, e);
        }
    }

    private boolean shouldSkipNotification(int startOffset, int endOffset,
                                           String text) {
        boolean isShort = endOffset - startOffset < settings
                .getNotifyFromTextLenght();
        return isShort && !FileUtils.isWholeFile(startOffset, endOffset, text);
    }

    private boolean shouldSkipFormatting(PsiFile psiFile, int startOffset,
                                         int endOffset) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (settings.isFormatSeletedTextInAllFileTypes()) {
            //when file is being edited, it is important to load text from editor, i think
            final Editor editor = PsiUtilBase.findEditor(psiFile);
            if (editor != null) {
                Document document = editor.getDocument();
                String text = document.getText();
                if (!FileUtils.isWholeFile(startOffset, endOffset, text)) {
                    return false;
                }
            }
        }
        if (settings.isFormatOtherFileTypesWithIntelliJ()) {
            return isDisabledFileType(virtualFile);
        }
        return true;
    }

    public boolean canReformatWithEclipse(PsiFile psiFile) {
        VirtualFile file = psiFile.getVirtualFile();
        Project project = psiFile.getProject();
        return file.isInLocalFileSystem() && FileUtils.isWritable(file, project)
                && fileTypeIsSupported(file);
    }

    private boolean isWholeFileOrSelectedText(PsiFile psiFile, int startOffset, int endOffset) {

        final Editor editor = PsiUtilBase.findEditor(psiFile);

        if (editor == null) {
            return true;
        } else {
            Document document = editor.getDocument();
            String text = document.getText();
            boolean wholeFile = FileUtils.isWholeFile(startOffset, endOffset, text);

            return editor.getSelectionModel().hasSelection() || wholeFile;
        }
    }

    private void formatWithIntelliJ(PsiFile psiFile, int startOffset,
                                    int endOffset) {
        original.reformatText(psiFile, startOffset, endOffset);
    }

    private boolean isDisabledFileType(VirtualFile virtualFile) {
        String path = virtualFile.getPath();
        DisabledFileTypeSettings disabledFileTypeSettings = settings
                .geDisabledFileTypeSettings();
        return disabledFileTypeSettings.isDisabled(path);
    }

    private boolean fileTypeIsSupported(@NotNull VirtualFile file) {
        return file.getPath().endsWith(".java");
    }
}
