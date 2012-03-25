package krasa.formatter.eclipse;

import java.io.File;

/**
 * @author Vojtech Krasa
 */
public class InvalidPathToConfigFileException extends RuntimeException {

    public InvalidPathToConfigFileException(File file) {
        super("File does not exists: " + file.getAbsolutePath());
    }
}
