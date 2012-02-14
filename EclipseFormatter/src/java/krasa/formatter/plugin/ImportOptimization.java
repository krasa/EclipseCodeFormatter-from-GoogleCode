package krasa.formatter.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import krasa.formatter.settings.JoinedGroup;
import krasa.formatter.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * @author Vojtech Krasa
 */
public class ImportOptimization {
    private static final Logger LOG = Logger
            .getInstance(ImportOptimization.class.getName());

    public static final int START_OF_IMPORTS_PACKAGE_DECLARATION = 7;
    @NotNull
    private Settings settings;

    public ImportOptimization(@NotNull Settings settings) {
        this.settings = settings;
    }

    public void byIntellij(PsiFile psiFile, Project project) {
        if (!settings.isOptimizeImports()) {
            return;
        }
        final PsiImportList newImportList = JavaCodeStyleManager.getInstance(
                project).prepareOptimizeImportsResult((PsiJavaFile) psiFile);

        try {
            final PsiDocumentManager manager = PsiDocumentManager
                    .getInstance(psiFile.getProject());
            final Document document = manager.getDocument(psiFile);
            if (document != null) {
                manager.commitDocument(document);
            }
            final PsiImportList oldImportList = ((PsiJavaFile) psiFile)
                    .getImportList();
            assert oldImportList != null;
            if (newImportList != null) {
                oldImportList.replace(newImportList);
            }
            manager.doPostponedOperationsAndUnblockDocument(document);
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    /**
     * appends blank lines between import groups
     */
    public void appendBlankLinesBetweenGroups(Document document) {
        if (!settings.isOptimizeImports()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(document.getText());
        String lastImportGroup = null;
        while (scanner.hasNext()) {
            String next = scanner.nextLine();
            if (next == null) {
                break;
            }
            if (next.startsWith("import ")) {
                int i = next.indexOf(".");
                if (isNotValidImport(i)) {
                    continue;
                }
                String currentImportGroup = next.substring(
                        START_OF_IMPORTS_PACKAGE_DECLARATION, i);
                if (shouldAppendBlankLine(lastImportGroup, currentImportGroup)) {
                    sb.append(Settings.LINE_SEPARATOR);
                }
                lastImportGroup = currentImportGroup;
            } else if (next.isEmpty()) {
                lastImportGroup = null;
            }
            append(sb, next);
        }
        document.setText(sb.toString());
    }

    private boolean isNotValidImport(int i) {
        return i <= START_OF_IMPORTS_PACKAGE_DECLARATION;
    }

    private boolean shouldAppendBlankLine(String lastImportGroup,
                                          String currentImportGroup) {
        if (lastImportGroup == null)
            return false;

        // TODO find out what is the eclipse's algorithm
        return !(lastImportGroup.equals(currentImportGroup) || isConfiguredToJoin(
                lastImportGroup, currentImportGroup));
    }

    private boolean isConfiguredToJoin(String lastImportGroup,
                                       String currentImportGroup) {
        return settings.getImportGroupSettings().contains(
                new JoinedGroup(lastImportGroup, currentImportGroup));
    }

    private void append(StringBuilder sb, String next) {
        sb.append(next);
        sb.append(Settings.LINE_SEPARATOR);
    }
}
