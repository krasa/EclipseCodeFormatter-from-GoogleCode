package krasa.formatter.common;

import krasa.formatter.eclipse.FileDoesNotExistsException;

import java.io.File;

/**
 * @author Vojtech Krasa
 */
public class ModifiableFile extends File {
    private long lastModified;

    public ModifiableFile(String pathToConfigFileJava) {
        super(pathToConfigFileJava);
    }

    public boolean wasChanged() {
        checkIfExists();
        return this.lastModified() > lastModified;
    }

    public void checkIfExists() throws FileDoesNotExistsException {
        if (!this.exists()) {
            throw new FileDoesNotExistsException(this);
        }
    }

    public void saveLastModified() {
        lastModified = this.lastModified();
    }

}
