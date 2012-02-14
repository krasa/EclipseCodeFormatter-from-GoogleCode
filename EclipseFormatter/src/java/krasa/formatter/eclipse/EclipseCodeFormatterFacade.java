package krasa.formatter.eclipse;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.IDocument;

import java.io.File;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class EclipseCodeFormatterFacade {
    protected final MyCodeFormatterApplication codeFormatterApplication;
    protected final String pathToConfigFile;
    protected CodeFormatter codeFormatter;
    private long lastModified;

    public EclipseCodeFormatterFacade(String pathToConfigFile) {
        codeFormatterApplication = new MyCodeFormatterApplication();
        this.pathToConfigFile = pathToConfigFile;
    }

    private CodeFormatter getCodeFormatter() throws InvalidPathToConfigFileException {
        File file = new File(this.pathToConfigFile);
        if (!file.exists()) {
            throw new InvalidPathToConfigFileException();
        }

        if (codeFormatter == null || configFileWasChanged(file)) {
            return newCodeFormatter(file);
        }
        return codeFormatter;
    }

    private CodeFormatter newCodeFormatter(File file) throws InvalidPathToConfigFileException {
        lastModified = file.lastModified();
        Properties properties = codeFormatterApplication.readConfig(file);

        if (properties.isEmpty()) {
            throw new InvalidPathToConfigFileException("incorrect properties file");
        }

        codeFormatter = ToolFactory.createCodeFormatter(properties);
        return codeFormatter;
    }

    private boolean configFileWasChanged(File file) {
        return file.lastModified() > lastModified;
    }

    public String format(File file, String lineSeparator) throws InvalidPathToConfigFileException {
        IDocument iDocument = codeFormatterApplication.formatWithoutWrite(file, getCodeFormatter(), lineSeparator);
        return iDocument.get();
    }

    /**
     * @param text          to format
     * @param startOffset   start of formatted area - this should be always start of line
     * @param endOffset     end of formatted area
     * @param lineSeparator - null for default
     */
    public String format(String text, int startOffset, int endOffset, String lineSeparator) throws InvalidPathToConfigFileException {
        if (endOffset > text.length()) {
            endOffset = text.length();
        }
        return codeFormatterApplication.format(text, getCodeFormatter(), startOffset, endOffset - startOffset, lineSeparator);
    }
}
