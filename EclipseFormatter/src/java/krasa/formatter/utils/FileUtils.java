package krasa.formatter.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vojtech Krasa
 */
public class FileUtils {

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
}
