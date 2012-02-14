package krasa.formatter.plugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import krasa.formatter.eclipse.EclipseCodeFormatterFacade;
import krasa.formatter.eclipse.InvalidPathToConfigFileException;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vojtech Krasa
 */
public class EclipseCodeFormatter {

    @NotNull
    private Settings settings;
    @NotNull
    private ImportOptimization importOptimization;
    @NotNull
    private Notifier notifier;
    @NotNull
    CodeStyleManager original;
    @NotNull
    protected final EclipseCodeFormatterFacade codeFormatterFacade;

    public EclipseCodeFormatter(@NotNull Settings settings, @NotNull Project project, CodeStyleManager original) {
        codeFormatterFacade = new EclipseCodeFormatterFacade(
                settings.getEclipsePrefs());
        this.importOptimization = new ImportOptimization(settings);
        this.settings = settings;
        this.notifier = new Notifier(project);
        this.original = original;
    }

    public void format(PsiFile psiFile, int startOffset, int endOffset) throws InvalidPathToConfigFileException {
        importOptimization.byIntellij(psiFile);
        formatWithEclipse(psiFile, startOffset, endOffset);
    }


    private void formatWithEclipse(PsiFile psiFile, int startOffset,
                                   int endOffset) throws InvalidPathToConfigFileException {
        final Editor editor = PsiUtilBase.findEditor(psiFile);
        boolean skipSuccessFormattingNotification = false;

        if (editor != null) {
            skipSuccessFormattingNotification = formatWhenEditorIsOpen(startOffset, endOffset, editor);
        } else {
            formatWhenEditorIsClosed(psiFile);
        }
        if (!skipSuccessFormattingNotification) {
            notifier.notifySuccessFormatting(psiFile, false);
        }
    }

    private void formatWhenEditorIsClosed(PsiFile psiFile) throws InvalidPathToConfigFileException {
        // editor is closed
        VirtualFile virtualFile = psiFile.getVirtualFile();
        FileDocumentManager fileDocumentManager = FileDocumentManager
                .getInstance();
        Document writeTo = fileDocumentManager.getDocument(virtualFile);
        fileDocumentManager.saveDocument(writeTo);
        writeTo.setText(codeFormatterFacade.format(FileUtils.ioFile(virtualFile),
                Settings.LINE_SEPARATOR));
        importOptimization.appendBlankLinesBetweenGroups(writeTo);
        fileDocumentManager.saveDocument(writeTo);
    }

    /*when file is being edited, it is important to load text from editor, i think */
    private boolean formatWhenEditorIsOpen(int startOffset, int endOffset, Editor editor) throws InvalidPathToConfigFileException {
        Document document = editor.getDocument();
        String text = document.getText();
        int fixedStartOffset = getLineStartOffset(startOffset, text);
        document.setText(codeFormatterFacade.format(text, fixedStartOffset,
                endOffset, Settings.LINE_SEPARATOR));
        importOptimization.appendBlankLinesBetweenGroups(document);

        return isSkipNotification(startOffset,
                endOffset, text);
    }

    /**
     * start offset must be on the start of line
     */
    private int getLineStartOffset(int startOffset, String text) {
        if (startOffset == 0) {
            return 0;
        }
        return text.substring(0, startOffset).lastIndexOf(
                Settings.LINE_SEPARATOR) + 1;
    }

    private boolean isSkipNotification(int startOffset, int endOffset,
                                       String text) {
        boolean isShort = endOffset - startOffset < settings
                .getNotifyFromTextLenght();
        return isShort && !FileUtils.isWholeFile(startOffset, endOffset, text);
    }


}
