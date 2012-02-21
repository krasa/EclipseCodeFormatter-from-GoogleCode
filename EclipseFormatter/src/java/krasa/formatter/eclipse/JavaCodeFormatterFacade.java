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
        File file = new File(this.pathToConfigFile);
        if (!file.exists()) {
            System.err.println(new File("").getAbsolutePath());
            throw new InvalidPathToConfigFileException();
        }

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


    public String format(String content, String lineSeparator) throws InvalidPathToConfigFileException {
        return formatInternal(content, 0, lineSeparator, content.length());
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
        return formatInternal(text, startOffset, lineSeparator, endOffset - startOffset);
    }

    private String formatInternal(String text, int startOffset, String lineSeparator, int length) throws InvalidPathToConfigFileException {
        IDocument doc = new Document();
        try {
            doc.set(text);
            TextEdit edit = getCodeFormatter().format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS, text,
                    startOffset, length, 0, lineSeparator);
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
