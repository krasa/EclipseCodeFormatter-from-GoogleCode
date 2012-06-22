package krasa.formatter.plugin.processor;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import krasa.formatter.plugin.Range;

/**
 * @author Vojtech Krasa
 */
public interface Processor {
    public boolean process(Document document, PsiFile psiFile, Range range);

}
