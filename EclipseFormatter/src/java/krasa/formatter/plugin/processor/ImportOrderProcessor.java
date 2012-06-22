package krasa.formatter.plugin.processor;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatementBase;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import krasa.formatter.common.ModifiableFile;
import krasa.formatter.eclipse.FileDoesNotExistsException;
import krasa.formatter.plugin.ImportSorter;
import krasa.formatter.plugin.ImportSorterException;
import krasa.formatter.plugin.InvalidPropertyFile;
import krasa.formatter.plugin.Range;
import krasa.formatter.settings.Settings;
import krasa.formatter.utils.FileUtils;
import krasa.formatter.utils.StringUtils;

import java.util.List;
import java.util.Properties;

/**
 * @author Vojtech Krasa
 */
public class ImportOrderProcessor implements Processor {
    private Settings settings;
    protected ImportSorter importSorter;
    ModifiableFile importOrderConfigFile;

    public ImportOrderProcessor(Settings settings) {
        this.settings = settings;
    }

    @Override
    public boolean process(final Document document, final PsiFile psiFile, final Range range) {
        CodeStyleManagerImpl.setSequentialProcessingAllowed(false);
//        final Runnable writeAction = new Runnable() {
//            @Override
//            public void run() {
        if (FileUtils.isJava(psiFile) && settings.isOptimizeImports() && range.isWholeFile()) {
            FileUtils.optimizeImportsByIntellij(psiFile);
            try {
                if (importSorter == null) {
                    importSorter = createImportSorter();
                } else {
                    if (settings.isImportOrderFromFile()) {
                        if (importOrderConfigFile == null) {
                            importOrderConfigFile = new ModifiableFile(settings.getImportOrderConfigFilePath());
                        }
                        if (importOrderConfigFile.wasChanged()) {
                            importSorter = createImportSorter();
                        }
                    }
                }
                importSorter.sortImports(document);
            } catch (FileDoesNotExistsException e) {
                throw e;
            } catch (InvalidPropertyFile e) {
                throw e;
            } catch (Exception e) {
                final PsiImportList oldImportList = ((PsiJavaFile) psiFile).getImportList();
                StringBuilder stringBuilder = new StringBuilder();
                if (oldImportList != null) {
                    PsiImportStatementBase[] allImportStatements = oldImportList.getAllImportStatements();
                    for (PsiImportStatementBase allImportStatement : allImportStatements) {
                        String text = allImportStatement.getText();
                        stringBuilder.append(text);
                    }
                }
                String message = "imports: " + stringBuilder.toString() + ", settings: " + settings.getImportOrder();
                throw new ImportSorterException(message, e);
            }
        }
//            }
//        };
//        
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                final Runnable writeRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        ApplicationManager.getApplication().runWriteAction(writeAction);
//                    }
//                };
//
//                ApplicationManager.getApplication().invokeLater(writeRunnable);
//            }
//        };
//        runnable.run();
//        ApplicationManager.getApplication().executeOnPooledThread(runnable);
        CodeStyleManagerImpl.setSequentialProcessingAllowed(true);
        return true;
    }

    private ImportSorter createImportSorter() {
        List<String> strings;
        if (settings.isImportOrderFromFile()) {
            importOrderConfigFile = new ModifiableFile(settings.getImportOrderConfigFilePath());
            Properties properties = FileUtils.readPropertiesFile(importOrderConfigFile);
            String property = properties.getProperty("org.eclipse.jdt.ui.importorder");
            if (property == null) {
                throw new InvalidPropertyFile("org.eclipse.jdt.ui.importorder", importOrderConfigFile);
            }
            strings = StringUtils.trimToList(property);
        } else {
            strings = settings.getImportOrderAsList();
        }
        return new ImportSorter(strings);
    }

}
