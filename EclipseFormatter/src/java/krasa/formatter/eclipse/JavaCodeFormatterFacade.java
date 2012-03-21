package krasa.formatter.eclipse;

import krasa.formatter.plugin.Notifier;
import krasa.formatter.settings.Settings;
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

    protected String formatInternal(String text, int startOffset, int endOffset) throws InvalidPathToConfigFileException {
        if (endOffset > text.length()) {
            endOffset = text.length();
        }
        IDocument doc = new Document();
        try {
            doc.set(text);
            /**
             * Format <code>source</code>,
             * and returns a text edit that correspond to the difference between the given
             * string and the formatted string.
             * <p>
             * It returns null if the given string cannot be formatted.
             * </p><p>
             * If the offset position is matching a whitespace, the result can include
             * whitespaces. It would be up to the caller to get rid of preceding
             * whitespaces.
             * </p>
             *
             * @param kind Use to specify the kind of the code snippet to format. It can
             * be any of these:
             * <ul>
             * 	<li>{@link #K_EXPRESSION}</li>
             * 	<li>{@link #K_STATEMENTS}</li>
             * 	<li>{@link #K_CLASS_BODY_DECLARATIONS}</li>
             * 	<li>{@link #K_COMPILATION_UNIT}<br>
             * 		<b>Since 3.4</b>, the comments can be formatted on the fly while
             * 		using this kind of code snippet<br>
             * 		(see {@link #F_INCLUDE_COMMENTS} for more detailed explanation on
             * 		this flag)
             * 	</li>
             * 	<li>{@link #K_UNKNOWN}</li>
             * 	<li>{@link #K_SINGLE_LINE_COMMENT}</li>
             * 	<li>{@link #K_MULTI_LINE_COMMENT}</li>
             * 	<li>{@link #K_JAVA_DOC}</li>
             * </ul>
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

            TextEdit edit = getCodeFormatter().format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS, text,
                    startOffset, endOffset - startOffset, 0, Settings.LINE_SEPARATOR);
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

    @Override
    protected Properties createDefaultConfig() {
        Properties defaultConfig = new Properties();
        // TODO: Ideally, the IntelliJ project's language level should be the default value.
        defaultConfig.setProperty("org.eclipse.jdt.core.compiler.source", "1.5");
        return defaultConfig;
    }

    @Override
    protected void validateConfig(Properties config) {
        String sourceVersionString = config.getProperty("org.eclipse.jdt.core.compiler.source");
        if (sourceVersionString != null) {
            float sourceVersion = 0;
            try {
                sourceVersion = Float.parseFloat(sourceVersionString);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Illegal value for org.eclipse.jdt.core.compiler.source property ("
                        + sourceVersionString + ") - supported Java source versions are 1.5, 1.6, 1.7, or 1.8.");
            }
            if (sourceVersion < 1.5) {
                throw new RuntimeException("Illegal value for org.eclipse.jdt.core.compiler.source property ("
                        + sourceVersionString + ") - Eclipse formatter requires a Java source version >= 1.5.");
            }
        }
    }
}
