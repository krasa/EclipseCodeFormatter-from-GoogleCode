package krasa.formatter.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import krasa.formatter.eclipse.InvalidPathToConfigFileException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class FileUtils {
    private static final Logger LOG = Logger.getInstance(FileUtils.class.getName());

    public static boolean isWritable(@NotNull VirtualFile file,
                                     @NotNull Project project) {
        return !ReadonlyStatusHandler.getInstance(project)
                .ensureFilesWritable(file).hasReadonlyFiles();
    }

    public static boolean isWholeFile(int startOffset, int endOffset, String text) {
        return startOffset == 0 && endOffset == text.length();
    }

    public static boolean isJavaScript(PsiFile psiFile) {
        return endsWith(psiFile, ".js");
    }

    public static boolean isJava(PsiFile psiFile) {
        return endsWith(psiFile, ".java");
    }

    public static boolean endsWith(PsiFile psiFile, String... suffix) {
        VirtualFile file = psiFile.getVirtualFile();
        for (String s : suffix) {
            if (file.getPath().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static void optimizeImportsByIntellij(PsiFile psiFile) {

        Project project = psiFile.getProject();
        final PsiImportList newImportList = JavaCodeStyleManager.getInstance(project).prepareOptimizeImportsResult(
                (PsiJavaFile) psiFile);

        try {
            final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
            final Document document = manager.getDocument(psiFile);
            if (document != null) {
                manager.commitDocument(document);
            }
            final PsiImportList oldImportList = ((PsiJavaFile) psiFile).getImportList();
            assert oldImportList != null;
            if (newImportList != null) {
                oldImportList.replace(newImportList);
            }
            manager.doPostponedOperationsAndUnblockDocument(document);
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    public static Properties readPropertiesFile(File file, Properties defaultConfig) {
        if (!file.exists()) {
            throw new InvalidPathToConfigFileException(file);

        }
        BufferedInputStream stream = null;
        final Properties formatterOptions;
        try {
            stream = new BufferedInputStream(new FileInputStream(file));
            formatterOptions = new Properties(defaultConfig);
            formatterOptions.load(stream);
        } catch (IOException e) {
            throw new RuntimeException("config file read error", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
        return formatterOptions;
    }

    public static Properties readPropertiesFile(File file) {
        return readPropertiesFile(file, null);
    }
}
