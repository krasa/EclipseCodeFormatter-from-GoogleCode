package krasa.formatter.plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.IncorrectOperationException;
import krasa.formatter.eclipse.EclipseCodeFormatterFacade;
import krasa.formatter.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Supported operations are handled by Eclipse formatter, other by IntelliJ formatter.
 * <p/>
 * TODO proper write action thread handling
 *
 * @author Vojtech Krasa
 * @since 30.10.20011
 */
public class EclipseCodeStyleManager extends DelegatingCodeStyleManager {
	public static final String GROUP_DISPLAY_ID = "Eclipse code formatter info";
	public static final String GROUP_DISPLAY_ID_ERROR = "Eclipse code formatter error";

	private static final Logger LOG = Logger.getInstance(EclipseCodeStyleManager.class.getName());
	public static final String LINE_SEPARATOR = "\n";

	@NotNull
	private final Project project;
	@NotNull
	protected final EclipseCodeFormatterFacade codeFormatterFacade;
	@NotNull
	private Settings settings;

	public EclipseCodeStyleManager(@NotNull CodeStyleManager original, @NotNull Settings settings, @NotNull Project project) {
		super(original);
		this.project = project;
		this.settings = settings;
		codeFormatterFacade = new EclipseCodeFormatterFacade(settings.getEclipsePrefs());
	}

	public void reformatText(@NotNull PsiFile psiFile, final int startOffset, final int endOffset) throws IncorrectOperationException {
		boolean formattedByIntelliJ = false;
		try {
			ApplicationManager.getApplication().assertWriteAccessAllowed();
			PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
			CheckUtil.checkWritable(psiFile);

			final VirtualFile file = psiFile.getVirtualFile();
			Project project = psiFile.getProject();

			final Editor editor = PsiUtilBase.findEditor(psiFile);
			if (file != null && !canReformatWithEclipse(file, project)) {
				formattedByIntelliJ = true;
				super.reformatText(psiFile, startOffset, endOffset);
			} else if (file != null) {
				FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
				if (editor != null) {

					int visualColumnToRestore = getVisualColumnToRestore(editor);

//					ApplicationManager.getApplication().runWriteAction(new Runnable() {
//						@Override
//						public void run() {
					Document document = editor.getDocument();
//					fileDocumentManager.saveDocument(document); DO NOT SAVE IT, IT BREAKS IT
					String text = document.getText();
					int fixedStartOffset = startOffset;
					//if there is selected text
					if (startOffset != 0) {
						//start offset must be on the start of line
						fixedStartOffset = text.substring(0, startOffset).lastIndexOf(LINE_SEPARATOR) + 1;
					}
					document.setText(codeFormatterFacade.format(text, fixedStartOffset, endOffset, LINE_SEPARATOR));


					restoreVisualColumnToRestore(editor, visualColumnToRestore);

//						}
//					});
				} else {
					Document writeTo = fileDocumentManager.getDocument(file);
					fileDocumentManager.saveDocument(writeTo);
					writeTo.setText(codeFormatterFacade.format(ioFile(file), LINE_SEPARATOR));
					fileDocumentManager.saveDocument(writeTo);
				}
			} else {
				notifyNothingWasFormatted();
				return;
			}
			notifySuccessFormatting(psiFile, formattedByIntelliJ);
		} catch (final Exception e) {
			e.printStackTrace();
			notifyFailedFormatting(psiFile, formattedByIntelliJ, e);
		}
	}

	private void restoreVisualColumnToRestore(Editor editor, int visualColumnToRestore) {
		if (visualColumnToRestore < 0) {
		} else {
			CaretModel caretModel = editor.getCaretModel();
			VisualPosition position = caretModel.getVisualPosition();
			if (visualColumnToRestore != position.column) {
				caretModel.moveToVisualPosition(new VisualPosition(position.line, visualColumnToRestore));
			}
		}
	}

	private int getVisualColumnToRestore(Editor editor) {
		int visualColumnToRestore = -1;

		if (editor != null) {
			Document document1 = editor.getDocument();
			int caretOffset = editor.getCaretModel().getOffset();
			caretOffset = Math.max(Math.min(caretOffset, document1.getTextLength() - 1), 0);
			CharSequence text1 = document1.getCharsSequence();
			int caretLine = document1.getLineNumber(caretOffset);
			int lineStartOffset = document1.getLineStartOffset(caretLine);
			int lineEndOffset = document1.getLineEndOffset(caretLine);
			boolean fixCaretPosition = true;
			for (int i = lineStartOffset; i < lineEndOffset; i++) {
				char c = text1.charAt(i);
				if (c != ' ' && c != '\t' && c != '\n') {
					fixCaretPosition = false;
					break;
				}
			}
			if (fixCaretPosition) {
				visualColumnToRestore = editor.getCaretModel().getVisualPosition().column;
			}
		}
		return visualColumnToRestore;
	}

	private void notifyNothingWasFormatted() {
		Notification notification = new Notification(GROUP_DISPLAY_ID,
				"",
				"Nothing was not formatted",
				NotificationType.WARNING);
		showNotification(notification);
	}

	private void notifyFailedFormatting(PsiFile psiFile, boolean formattedByIntelliJ, Exception e) {
		String error = e.getMessage() == null ? "" : e.getMessage();
		if (!formattedByIntelliJ) {
			Notification notification = new Notification(GROUP_DISPLAY_ID,
					"",
					psiFile.getName() + " failed to format with Eclipse code formatter. " + error,
					NotificationType.ERROR);
			showNotification(notification);
		} else {
			Notification notification = new Notification(GROUP_DISPLAY_ID,
					"",
					psiFile.getName() + " failed to format with IntelliJ code formatter. " + error,
					NotificationType.ERROR);
			showNotification(notification);
		}
	}

	private void notifySuccessFormatting(PsiFile psiFile, boolean formattedByIntelliJ) {
		if (formattedByIntelliJ) {
			Notification notification = new Notification(GROUP_DISPLAY_ID,
					"",
					psiFile.getName() + " formatted sucessfully by IntelliJ code formatter",
					NotificationType.WARNING);
			showNotification(notification);
		} else {
			Notification notification = new Notification(GROUP_DISPLAY_ID,
					"",
					psiFile.getName() + " formatted sucessfully by Eclipse code formatter",
					NotificationType.INFORMATION);
			showNotification(notification);
		}
	}


	private void showNotification(final Notification notification) {
		ApplicationManager.getApplication().invokeLater(new Runnable() {
			@Override
			public void run() {
				Notifications.Bus.notify(notification, project);
			}
		});
	}

	private boolean canReformatWithEclipse(@NotNull VirtualFile file, @NotNull Project project) {
		return file.isInLocalFileSystem()
				&& isWritable(file, project)
				&& fileTypeIsSupported(file);
	}

	private static boolean isWritable(@NotNull VirtualFile file, @NotNull Project project) {
		return !ReadonlyStatusHandler.getInstance(project).ensureFilesWritable(file).hasReadonlyFiles();
	}

	private boolean fileTypeIsSupported(@NotNull VirtualFile file) {
		return ioFile(file).getPath().endsWith(".java");
	}

	@Deprecated //probably not needed anymore
	private static boolean wholeFile(@NotNull PsiFile file, int startOffset, int endOffset) {
		return startOffset == 0
				&& endOffset == file.getTextRange().getEndOffset();
	}

	@NotNull
	private static File ioFile(@NotNull VirtualFile file) {
		return new File(file.getPath());
	}
}
