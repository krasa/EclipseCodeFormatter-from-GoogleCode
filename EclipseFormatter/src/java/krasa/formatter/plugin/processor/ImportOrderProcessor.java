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
import krasa.formatter.settings.provider.ImportOrderProvider;
import krasa.formatter.utils.FileUtils;

/**
 * @author Vojtech Krasa
 */
public class ImportOrderProcessor implements Processor {
	private Settings settings;
	protected ImportSorter importSorter;
	protected ImportOrderProvider importOrderProviderFromFile;
	protected ModifiableFile.Monitor modifiedMonitor;

	public ImportOrderProcessor(Settings settings) {
		this.settings = settings;
		importOrderProviderFromFile = new ImportOrderProvider(settings);
	}

	public ImportOrderProcessor(Settings settings, ImportSorter importSorter, ImportOrderProvider importOrderProviderFromFile, ModifiableFile.Monitor modifiedMonitor) {
		this.settings = settings;
		this.importSorter = importSorter;
		this.importOrderProviderFromFile = importOrderProviderFromFile;
		this.modifiedMonitor = modifiedMonitor;
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
				getImportSorter().sortImports(document);
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

	protected ImportSorter getImportSorter() {
		if (settings.isImportOrderFromFile()) {
			if (importOrderProviderFromFile.wasChanged(modifiedMonitor)) {
				modifiedMonitor = importOrderProviderFromFile.getModifiedMonitor();
				importSorter = new ImportSorter(importOrderProviderFromFile.get());
			}
		} else if (importSorter == null) {
			importSorter = new ImportSorter(settings.getImportOrderAsList());
		}
		return importSorter;
	}


}
