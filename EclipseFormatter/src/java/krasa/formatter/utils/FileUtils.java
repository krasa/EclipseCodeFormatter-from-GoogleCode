package krasa.formatter.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Vojtech Krasa
 */
public class FileUtils {

    @NotNull
    public static File ioFile(@NotNull VirtualFile file) {
        return new File(file.getPath());
    }

    public static boolean isWritable(@NotNull VirtualFile file,
                                     @NotNull Project project) {
        return !ReadonlyStatusHandler.getInstance(project)
                .ensureFilesWritable(file).hasReadonlyFiles();
    }

    public static boolean isWholeFile(int startOffset, int endOffset, String text) {
        return startOffset == 0 && endOffset == text.length();
    }

}
