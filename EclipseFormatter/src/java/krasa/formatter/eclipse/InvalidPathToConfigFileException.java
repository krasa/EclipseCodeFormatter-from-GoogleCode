package krasa.formatter.eclipse;

import krasa.formatter.plugin.Notifier;

/**
 * @author Vojtech Krasa
 */
public class InvalidPathToConfigFileException extends Exception {

    public InvalidPathToConfigFileException() {
        super(Notifier.FILE_DOES_NOT_EXISTS);
    }

    public InvalidPathToConfigFileException(Throwable cause) {
        super(cause);
    }

    public InvalidPathToConfigFileException(String message) {
        super(message);
    }

    public InvalidPathToConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
