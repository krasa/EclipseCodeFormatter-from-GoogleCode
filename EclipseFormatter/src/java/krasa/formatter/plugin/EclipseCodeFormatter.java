package krasa.formatter.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatementBase;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiUtilBase;
import krasa.formatter.eclipse.CodeFormatterFacade;
import krasa.formatter.eclipse.InvalidPathToConfigFileException;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vojtech Krasa
 */
public class EclipseCodeFormatter {
    private static final Logger LOG = Logger.getInstance(EclipseCodeFormatter.class.getName());

    @NotNull
    private Settings settings;
    @NotNull
    private ImportOptimization importOptimization;
    @NotNull
    private Notifier notifier;
    @NotNull
    CodeStyleManager original;
    @NotNull
    Project project;
    @NotNull
    protected final CodeFormatterFacade codeFormatterFacade;

    public EclipseCodeFormatter(@NotNull Settings settings, @NotNull Project project, CodeStyleManager original, CodeFormatterFacade codeFormatterFacade1) {
        codeFormatterFacade = codeFormatterFacade1;
        this.importOptimization = new ImportOptimization(settings);
        this.settings = settings;
        this.notifier = new Notifier(project);
        this.project = project;
        this.original = original;
    }

    public void format(PsiFile psiFile, int startOffset, int endOffset) throws InvalidPathToConfigFileException {
        boolean wholeFile = FileUtils.isWholeFile(startOffset, endOffset, psiFile.getText());
        preProcess(psiFile, wholeFile);
        formatWithEclipse(psiFile, startOffset, endOffset, wholeFile);
    }

    private void preProcess(PsiFile psiFile, boolean wholeFile) {
        if (wholeFile && FileUtils.isJava(psiFile)) {
            importOptimization.byIntellij(psiFile);
        }
        ;
    }

    private void formatWithEclipse(PsiFile psiFile, int startOffset, int endOffset, boolean wholeFile)
            throws InvalidPathToConfigFileException {
        final Editor editor = PsiUtilBase.findEditor(psiFile);
        if (editor != null) {
            formatWhenEditorIsOpen(startOffset, endOffset, psiFile, wholeFile);
        } else {
            formatWhenEditorIsClosed(psiFile);
        }

    }

    private void formatWhenEditorIsClosed(PsiFile psiFile) throws InvalidPathToConfigFileException {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(virtualFile);
        fileDocumentManager.saveDocument(document); // when file is edited and editor is closed, it is needed to save
        // the text
        document.setText(reformat(document.getText()));
        postProcess(document, true, psiFile);
        fileDocumentManager.saveDocument(document);
    }

    private String reformat(String virtualFile) throws InvalidPathToConfigFileException {
        return codeFormatterFacade.format(virtualFile, Settings.LINE_SEPARATOR);
    }

    /* when file is being edited, it is important to load text from editor, i think */
    private void formatWhenEditorIsOpen(int startOffset, int endOffset, PsiFile file, boolean wholeFile)
            throws InvalidPathToConfigFileException {
        final Editor editor = PsiUtilBase.findEditor(file);
        int visualColumnToRestore = getVisualColumnToRestore(editor);

        Document document = editor.getDocument();
        String text = document.getText();
        document.setText(reformat(startOffset, endOffset, text));
        postProcess(document, wholeFile, file);

        restoreVisualColumn(editor, visualColumnToRestore);
    }

    private void postProcess(Document document, boolean wholeFile, PsiFile psiFile) {
        if (FileUtils.isJava(psiFile)) {
            if (!settings.isOptimizeImports()) {
                return;
            }
            if (!wholeFile) {
                return;
            }
            if (settings.isNewImportOptimizer()) {
                ImportSorter importSorter = null;
                try {
                    importSorter = new ImportSorter(settings.getImportOrderAsList());
                    importSorter.process(document);
                } catch (Exception e) {
                    final PsiImportList oldImportList = ((PsiJavaFile) psiFile).getImportList();
                    StringBuilder stringBuilder = new StringBuilder();
                    if (oldImportList != null) {
                        PsiImportStatementBase[] allImportStatements = oldImportList.getAllImportStatements();
                        for (PsiImportStatementBase allImportStatement : allImportStatements) {
                            String text = allImportStatement.getText();
                            stringBuilder.append(text);
                        }
                    }
                    String message = "imports: " + stringBuilder.toString() + ", settings: " + settings.getImportOrder();
                    throw new ImportSorterException(message, e);
                }
            } else {
                importOptimization.appendBlankLinesBetweenGroups(document);
            }
        }
    }

    private String reformat(int startOffset, int endOffset, String text) throws InvalidPathToConfigFileException {
        return codeFormatterFacade.format(text, getLineStartOffset(startOffset, text), endOffset,
                Settings.LINE_SEPARATOR);
    }

    /**
     * start offset must be on the start of line
     */
    private int getLineStartOffset(int startOffset, String text) {
        if (startOffset == 0) {
            return 0;
        }
        return text.substring(0, startOffset).lastIndexOf(Settings.LINE_SEPARATOR) + 1;
    }

    private void restoreVisualColumn(Editor editor, int visualColumnToRestore) {
        if (visualColumnToRestore < 0) {
        } else {
            CaretModel caretModel = editor.getCaretModel();
            VisualPosition position = caretModel.getVisualPosition();
            if (visualColumnToRestore != position.column) {
                caretModel.moveToVisualPosition(new VisualPosition(position.line, visualColumnToRestore));
            }
        }
    }

    // There is a possible case that cursor is located at the end of the line that contains only white spaces. For
    // example:
    // public void foo() {
    // <caret>
    // }
    // Formatter removes such white spaces, i.e. keeps only line feed symbol. But we want to preserve caret position
    // then.
    // So, we check if it should be preserved and restore it after formatting if necessary

    private int getVisualColumnToRestore(Editor editor) {
        int visualColumnToRestore = -1;

        if (editor != null) {
            Document document1 = editor.getDocument();
            int caretOffset = editor.getCaretModel().getOffset();
            caretOffset = Math.max(Math.min(caretOffset, document1.getTextLength() - 1), 0);
            CharSequence text1 = document1.getCharsSequence();
            int caretLine = document1.getLineNumber(caretOffset);
            int lineStartOffset = document1.getLineStartOffset(caretLine);
            int lineEndOffset = document1.getLineEndOffset(caretLine);
            boolean fixCaretPosition = true;
            for (int i = lineStartOffset; i < lineEndOffset; i++) {
                char c = text1.charAt(i);
                if (c != ' ' && c != '\t' && c != '\n') {
                    fixCaretPosition = false;
                    break;
                }
            }
            if (fixCaretPosition) {
                visualColumnToRestore = editor.getCaretModel().getVisualPosition().column;
            }
        }
        return visualColumnToRestore;
    }

}
