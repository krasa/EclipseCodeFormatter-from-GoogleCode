package krasa.formatter.eclipse;

import krasa.formatter.plugin.InvalidPropertyFile;
import krasa.formatter.plugin.Notifier;
import krasa.formatter.settings.Settings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class JSCodeFormatterFacade extends CodeFormatterFacade {
    protected final String pathToConfigFile;
    protected org.eclipse.wst.jsdt.core.formatter.CodeFormatter codeFormatter;
    private long lastModified;

    public JSCodeFormatterFacade(String pathToConfigFile) {
        this.pathToConfigFile = pathToConfigFile;
    }

    private org.eclipse.wst.jsdt.core.formatter.CodeFormatter getCodeFormatter() throws InvalidPathToConfigFileException {
        File file = checkIfExists(this.pathToConfigFile);

        if (codeFormatter == null || configFileWasChanged(file)) {
            return newCodeFormatter(file);
        }
        return codeFormatter;
    }

    private org.eclipse.wst.jsdt.core.formatter.CodeFormatter newCodeFormatter(File file) throws InvalidPropertyFile {
        lastModified = file.lastModified();
        Properties properties = readConfig(file);

        if (properties.isEmpty()) {
            throw new InvalidPropertyFile(file);
        }

        codeFormatter = org.eclipse.wst.jsdt.core.ToolFactory.createCodeFormatter(properties);
        return codeFormatter;
    }

    private boolean configFileWasChanged(File file) {
        return file.lastModified() > lastModified;
    }

    protected String formatInternal(String text, int startOffset, int endOffset) throws InvalidPathToConfigFileException {
        IDocument doc = new Document();
        try {
            // format the file (the meat and potatoes)
            doc.set(text);
            /**
             * Format <code>source</code>,
             * and returns a text edit that correspond to the difference between the given string and the formatted string.
             * <p>It returns null if the given string cannot be formatted.</p>
             *
             * <p>If the offset position is matching a whitespace, the result can include whitespaces. It would be up to the
             * caller to get rid of preceeding whitespaces.</p>
             *
             * @param kind Use to specify the kind of the code snippet to format. It can be any of these:
             *        K_EXPRESSION, K_STATEMENTS, K_CLASS_BODY_DECLARATIONS, K_JAVASCRIPT_UNIT, K_UNKNOWN,
             *        K_SINGLE_LINE_COMMENT, K_MULTI_LINE_COMMENT, K_JAVA_DOC
             * @param source the source to format
             * @param offset the given offset to start recording the edits (inclusive).
             * @param length the given length to stop recording the edits (exclusive).
             * @param indentationLevel the initial indentation level, used
             *      to shift left/right the entire source fragment. An initial indentation
             *      level of zero or below has no effect.
             * @param lineSeparator the line separator to use in formatted source,
             *     if set to <code>null</code>, then the platform default one will be used.
             * @return the text edit
             * @throws IllegalArgumentException if offset is lower than 0, length is lower than 0 or
             * length is greater than source length.
             */
            TextEdit edit = getCodeFormatter().format(org.eclipse.wst.jsdt.core.formatter.CodeFormatter.K_JAVASCRIPT_UNIT, text, startOffset, endOffset - startOffset, 0, Settings.LINE_SEPARATOR);
            if (edit != null) {
                edit.apply(doc);
            } else {
                throw new RuntimeException(
                        Notifier.FORMATTING_FAILED_PROBABLY_DUE_TO_NOT_COMPILABLE_CODE_OR_WRONG_CONFIG_FILE);
            }

            return doc.get();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }


}
