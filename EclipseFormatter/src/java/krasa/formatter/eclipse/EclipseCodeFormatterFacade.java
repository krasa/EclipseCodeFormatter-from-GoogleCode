package krasa.formatter.eclipse;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.IDocument;

import java.io.File;
import java.util.Properties;

/**
 * TODO it would be nice to cache CodeFormatter
 *
 * @author Vojtech Krasa
 */
public class EclipseCodeFormatterFacade {
    protected final MyCodeFormatterApplication codeFormatterApplication;
    protected final String pathToConfigFile;

    public EclipseCodeFormatterFacade(String pathToConfigFile) {
        codeFormatterApplication = new MyCodeFormatterApplication();

        this.pathToConfigFile = pathToConfigFile;
        newCodeFormatter(pathToConfigFile);
    }

    private CodeFormatter newCodeFormatter(String pathToConfigFile) {
        Properties properties = codeFormatterApplication
                .readConfig(pathToConfigFile);
        if (properties.isEmpty()) {
            throw new IllegalStateException("incorrect properties file");
        }

        CodeFormatter codeFormatter = ToolFactory
                .createCodeFormatter(properties);
        return codeFormatter;
    }

    public String format(File file) {
        return format(file, null);
    }

    public String format(File file, String lineSeparator) {
        IDocument iDocument = codeFormatterApplication.formatWithoutWrite(file,
                newCodeFormatter(pathToConfigFile), lineSeparator);
        return iDocument.get();
    }

    /**
     * @param text          to format
     * @param startOffset   start of formatted area - this should be always start of line
     * @param endOffset     end of formatted area
     * @param lineSeparator - null for default
     */
    public String format(String text, int startOffset, int endOffset,
                         String lineSeparator) {
        if (endOffset > text.length()) {
            endOffset = text.length();
        }
        return codeFormatterApplication.format(text,
                newCodeFormatter(pathToConfigFile), startOffset, endOffset
                - startOffset, lineSeparator);
    }
}
