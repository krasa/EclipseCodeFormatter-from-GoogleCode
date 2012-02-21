package krasa.formatter.eclipse;

import krasa.formatter.plugin.Notifier;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class JavaCodeFormatterFacade extends CodeFormatterFacade {
    protected final String pathToConfigFile;
    protected CodeFormatter codeFormatter;
    private long lastModified;

    public JavaCodeFormatterFacade(String pathToConfigFile) {
        this.pathToConfigFile = pathToConfigFile;
    }

    private CodeFormatter getCodeFormatter() throws InvalidPathToConfigFileException {
        File file = checkIfExists(this.pathToConfigFile);

        if (codeFormatter == null || configFileWasChanged(file)) {
            return newCodeFormatter(file);
        }
        return codeFormatter;
    }

    private CodeFormatter newCodeFormatter(File file) throws InvalidPathToConfigFileException {
        lastModified = file.lastModified();
        Properties properties = readConfig(file);

        if (properties.isEmpty()) {
            throw new InvalidPathToConfigFileException("incorrect properties file");
        }

        codeFormatter = ToolFactory.createCodeFormatter(properties);
        return codeFormatter;
    }

    private boolean configFileWasChanged(File file) {
        return file.lastModified() > lastModified;
    }

    protected String formatInternal(String text, int startOffset, int endOffset, String lineSeparator) throws InvalidPathToConfigFileException {
        if (endOffset > text.length()) {
            endOffset = text.length();
        }
        IDocument doc = new Document();
        try {
            doc.set(text);
            TextEdit edit = getCodeFormatter().format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS, text,
                    startOffset, endOffset, 0, lineSeparator);
            if (edit != null) {
                edit.apply(doc);
            } else {
                throw new RuntimeException(Notifier.FORMATTING_FAILED_PROBABLY_DUE_TO_NOT_COMPILABLE_CODE_OR_WRONG_CONFIG_FILE);
            }
            return doc.get();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}
