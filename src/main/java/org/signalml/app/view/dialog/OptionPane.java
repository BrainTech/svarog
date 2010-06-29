/* OptionPane.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Component;
import java.io.File;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.MutableDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.PrettyStringLocaleWrapper;
import org.signalml.util.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;

/** OptionPane
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OptionPane extends JOptionPane {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OptionPane.class);

	private static boolean initialized = false;

	private static MessageSourceAccessor messageSource = null;

	private static String errorString = null;
	private static String messageString = null;
	private static String okString = null;
	private static String applyString = null;
	private static String cancelString = null;
	private static String reloadString = null;
	private static String overwriteString = null;
	private static String openString = null;
	private static String saveString = null;
	private static String discardString = null;
	private static String closeString = null;
	private static String warningString = null;
	private static String createString = null;

	public static MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public static void setMessageSource(MessageSourceAccessor messageSource) {
		OptionPane.messageSource = messageSource;
	}

	private static void initialize() {

		okString = messageSource.getMessage("ok");
		applyString = messageSource.getMessage("apply");
		cancelString = messageSource.getMessage("cancel");
		errorString = messageSource.getMessage("error");
		messageString = messageSource.getMessage("message");
		reloadString = messageSource.getMessage("reload");
		overwriteString = messageSource.getMessage("overwrite");
		openString = messageSource.getMessage("open");
		saveString = messageSource.getMessage("save");
		discardString = messageSource.getMessage("discard");
		closeString = messageSource.getMessage("close");
		warningString = messageSource.getMessage("warning");
		createString = messageSource.getMessage("create");

		initialized = true;

	}

	public static void showError(Component parent, String message) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage(message),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showError(Component parent, String message, Object[] arguments) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage(message, arguments),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showRawError(Component parent, String message) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        message,
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showMessage(Component parent, String message) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage(message),
		        messageString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.INFORMATION_MESSAGE,
		        IconUtils.getInfoIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showRawMessage(Component parent, String message) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        message,
		        messageString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.INFORMATION_MESSAGE,
		        IconUtils.getInfoIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showException(Component parent, String message, Exception ex) {
		if (!initialized) {
			initialize();
		}
		String exMessage = messageSource.getMessage("exception."+ex.getClass().getName(), ex.getMessage());
		showOptionDialog(
		        parent,
		        messageSource.getMessage(message, new Object[] {exMessage}),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static int showDocumentAlreadyOpened(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.documentAlreadyOpen"),
		                  reloadString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {reloadString,cancelString},
		                  reloadString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showTaskRunning(Component parent) {
		return showProceedOption(parent, "situation.tasksWillBeAborted");
	}

	public static int showArtifactProjectDoesntExist(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("artifactMethod.situation.artifactProjectDoesntExist"),
		                  createString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {createString,cancelString},
		                  createString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showArtifactProjectExists(Component parent, String name) {
		return showReuseReplaceOption(parent, "artifactMethod.situation.artifactProjectExists", new Object[] { name });
	}

	public static int showStagerProjectExists(Component parent, String name) {
		return showReuseReplaceOption(parent, "stagerMethod.situation.stagerProjectExists", new Object[] { name });
	}

	public static int showDirectoryDoesntExistCreate(Component parent, File file) {
		if (!initialized) {
			initialize();
		}

		String path = file.getAbsolutePath();
		if (path.length() > 60) {
			path = path.substring(0, 20) + " ... " + path.substring(path.length() - 40);
		}
		String message = messageSource.getMessage("situation.directoryNotFoundWithNameCreate", new Object[] { path });

		int res = showOptionDialog(
		                  parent,
		                  message,
		                  createString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {createString,cancelString},
		                  createString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showDocumentUnsaved(Component parent, MutableDocument document) {
		if (!initialized) {
			initialize();
		}

		String message = null;

		if (document instanceof FileBackedDocument) {
			File file = ((FileBackedDocument) document).getBackingFile();
			if (file != null) {
				message = messageSource.getMessage("situation.documentUnsaved", new Object[] { file.getName() });
			}
		}

		if (message == null) {
			message = messageSource.getMessage("situation.newDocumentUnsaved");
		}

		int res = showOptionDialog(
		                  parent,
		                  message,
		                  saveString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {saveString,discardString,cancelString},
		                  saveString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
			return NO_OPTION;
		case 2:
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showNormalizationUnavoidable(Component parent) {
		if (!initialized) {
			initialize();
		}

		String normalizeString = messageSource.getMessage("normalize");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.normalizationUnavoidable"),
		                  normalizeString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {normalizeString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showProceedOption(Component parent, String messageCode) {
		if (!initialized) {
			initialize();
		}

		String proceedString = messageSource.getMessage("proceed");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage(messageCode),
		                  proceedString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {proceedString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showProceedOption(Component parent, String messageCode, Object[] args) {
		if (!initialized) {
			initialize();
		}

		String proceedString = messageSource.getMessage("proceed");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage(messageCode, args),
		                  proceedString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {proceedString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showReuseReplaceOption(Component parent, String messageCode, Object[] args) {
		if (!initialized) {
			initialize();
		}

		String reuseString = messageSource.getMessage("reuse");
		String replaceString = messageSource.getMessage("replace");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage(messageCode, args),
		                  reuseString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {reuseString,replaceString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
			return NO_OPTION;
		case 2 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showSerializableTaskRunning(Component parent) {
		if (!initialized) {
			initialize();
		}

		String suspendString = messageSource.getMessage("situation.serializableTasksRunningSuspend");
		String abortString = messageSource.getMessage("situation.serializableTasksRunningAbort");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.serializableTasksRunning"),
		                  suspendString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {suspendString,abortString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
			return NO_OPTION;
		case 2 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showRawConfigWillBeLost(Component parent) {
		return showProceedOption(parent, "situation.rawConfigWillBeLost");
	}

	public static int showAbortAllTasks(Component parent) {
		return showProceedOption(parent, "situation.abortAllTasks");
	}

	public static int showSuspendAllTasks(Component parent) {
		return showProceedOption(parent, "situation.suspendAllTasks");
	}

	public static int showResumeAllTasks(Component parent) {
		return showProceedOption(parent, "situation.resumeAllTasks");
	}

	public static int showRemoveAllTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllTasks");
	}

	public static int showRemoveAllFinishedTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllFinishedTasks");
	}

	public static int showRemoveAllAbortedTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllAbortedTasks");
	}

	public static int showRemoveAllFailedTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllFailedTasks");
	}

	public static int showFileAlreadyExists(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.fileAlreadyExists"),
		                  overwriteString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {overwriteString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showFileAlreadyExists(Component parent, String fileName) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.fileAlreadyExistsWithName", new Object[] { fileName }),
		                  overwriteString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {overwriteString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showResumeRestoredTasks(Component parent) {
		if (!initialized) {
			initialize();
		}

		String resumeString = messageSource.getMessage("situation.resumeRestoredTasksResume");
		String dontResumeString = messageSource.getMessage("situation.resumeRestoredTasksDontResume");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.resumeRestoredTasks"),
		                  overwriteString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {resumeString,dontResumeString},
		                  resumeString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showTagNotCompatible(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.tagNotCompatible"),
		                  openString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {openString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showTagChecksumBad(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.tagChecksumBad"),
		                  openString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {openString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return OK_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showOtherDocumentsDepend(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.otherDocumentsDepend"),
		                  closeString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {closeString,cancelString},
		                  closeString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
		case CLOSED_OPTION :
		default :
			return NO_OPTION;
		}

	}

	public static void showDocumentAlreadyOpenError(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.saveAsPathOpen"),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showFileNotFound(Component parent, File file) {
		if (!initialized) {
			initialize();
		}

		String message = null;
		if (file == null) {
			message = messageSource.getMessage("situation.fileNotFound");
		} else {
			String path = file.getAbsolutePath();
			if (path.length() > 60) {
				path = path.substring(0, 20) + " ... " + path.substring(path.length() - 40);
			}
			message = messageSource.getMessage("situation.fileNotFoundWithName", new Object[] { path });
		}

		showOptionDialog(
		        parent,
		        message,
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showDirectoryNotFound(Component parent, File file) {
		if (!initialized) {
			initialize();
		}

		String message = null;
		if (file == null) {
			message = messageSource.getMessage("situation.directoryNotFound");
		} else {
			String path = file.getAbsolutePath();
			if (path.length() > 60) {
				path = path.substring(0, 20) + " ... " + path.substring(path.length() - 40);
			}
			message = messageSource.getMessage("situation.directoryNotFoundWithName", new Object[] { path });
		}

		showOptionDialog(
		        parent,
		        message,
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showDirectoryNotAccessible(Component parent, File file) {
		if (!initialized) {
			initialize();
		}

		String message = null;
		if (file == null) {
			message = messageSource.getMessage("situation.directoryNotAccessible");
		} else {
			String path = file.getAbsolutePath();
			if (path.length() > 60) {
				path = path.substring(0, 20) + " ... " + path.substring(path.length() - 40);
			}
			message = messageSource.getMessage("situation.directoryNotAccessibleWithName", new Object[] { path });
		}

		showOptionDialog(
		        parent,
		        message,
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showDirectoryNotCreated(Component parent, File file) {
		if (!initialized) {
			initialize();
		}

		String message = null;
		if (file == null) {
			message = messageSource.getMessage("situation.directoryNotCreated");
		} else {
			String path = file.getAbsolutePath();
			if (path.length() > 60) {
				path = path.substring(0, 20) + " ... " + path.substring(path.length() - 40);
			}
			message = messageSource.getMessage("situation.directoryNotCreatedWithName", new Object[] { path });
		}

		showOptionDialog(
		        parent,
		        message,
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showDefaultMontageNotCompatible(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.defaultMontageNotCompatible"),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showPresetNotCompatible(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.presetNotCompatible"),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.WARNING_MESSAGE,
		        IconUtils.getWarningIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showTagStyleInUse(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.tagStyleInUse"),
		        errorString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.ERROR_MESSAGE,
		        IconUtils.getErrorIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static int showTagStyleModified(Component parent) {
		if (!initialized) {
			initialize();
		}

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.tagStyleModified"),
		                  applyString + "?",
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {applyString,discardString,cancelString},
		                  applyString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
			return NO_OPTION;
		case 2:
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showMontageDifferentOnTagSave(Component parent) {

		if (!initialized) {
			initialize();
		}

		String saveOriginalString = messageSource.getMessage("situation.montageDifferentOnTagSave.saveOriginal");
		String saveCurrentString = messageSource.getMessage("situation.montageDifferentOnTagSave.saveCurrent");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.montageDifferentOnTagSave"),
		                  messageSource.getMessage("situation.montageDifferentOnTagSave.title"),
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {saveOriginalString,saveCurrentString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
			return NO_OPTION;
		case 2:
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static int showMontageDifferentOnTagLoad(Component parent) {

		if (!initialized) {
			initialize();
		}

		String loadFromTagString = messageSource.getMessage("situation.montageDifferentOnTagLoad.loadFromTag");
		String keepCurrentString = messageSource.getMessage("situation.montageDifferentOnTagLoad.keepCurrent");

		int res = showOptionDialog(
		                  parent,
		                  messageSource.getMessage("situation.montageDifferentOnTagLoad"),
		                  messageSource.getMessage("situation.montageDifferentOnTagLoad.title"),
		                  JOptionPane.OK_OPTION,
		                  JOptionPane.QUESTION_MESSAGE,
		                  IconUtils.getQuestionIcon(),
		                  new Object[] {loadFromTagString,keepCurrentString,cancelString},
		                  cancelString
		          );

		switch (res) {
		case 0 :
			return YES_OPTION;
		case 1 :
			return NO_OPTION;
		case 2:
		case CLOSED_OPTION :
		default :
			return CANCEL_OPTION;
		}

	}

	public static void showNoActiveSignal(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.noActiveSignal"),
		        warningString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.WARNING_MESSAGE,
		        IconUtils.getWarningIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showNoActiveBook(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.noActiveBook"),
		        warningString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.WARNING_MESSAGE,
		        IconUtils.getWarningIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showNoActiveTag(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.noActiveTag"),
		        warningString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.WARNING_MESSAGE,
		        IconUtils.getWarningIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static void showNoSignalSelection(Component parent) {
		if (!initialized) {
			initialize();
		}
		showOptionDialog(
		        parent,
		        messageSource.getMessage("situation.noSignalSelection"),
		        warningString,
		        JOptionPane.OK_OPTION,
		        JOptionPane.WARNING_MESSAGE,
		        IconUtils.getWarningIcon(),
		        new Object[] {okString},
		        okString
		);
	}

	public static Locale showLanguageOption() {

		Locale[] locales = SvarogConstants.AVAILABLE_LOCALES;
		PrettyStringLocaleWrapper[] possibilities = new PrettyStringLocaleWrapper[locales.length];
		Locale defaultLocale = Locale.getDefault();
		PrettyStringLocaleWrapper defaultPossibility = new PrettyStringLocaleWrapper(defaultLocale);
		boolean defAvailable = false;

		for (int i=0; i<locales.length; i++) {
			possibilities[i] = new PrettyStringLocaleWrapper(locales[i]);
			if (!defAvailable && locales[i].equals(defaultLocale)) {
				defaultLocale = locales[i];
				defaultPossibility = possibilities[i];
				defAvailable = true;
			}
		}
		if (!defAvailable) {
			defaultLocale = locales[0];
			defaultPossibility = possibilities[0];
		}
		PrettyStringLocaleWrapper s = (PrettyStringLocaleWrapper) showInputDialog(
		                                      null,
		                                      "",
		                                      SvarogConstants.NAME,
		                                      JOptionPane.QUESTION_MESSAGE,
		                                      IconUtils.getQuestionIcon(),
		                                      possibilities,
		                                      defaultPossibility
		                              );
		if (s == null) {
			return null;
		}
		return s.getLocale();
	}

	public static String showPresetNameOption(Component parent, String name) {
		if (!initialized) {
			initialize();
		}

		String s = (String) showInputDialog(
		                   parent,
		                   messageSource.getMessage("name"),
		                   messageSource.getMessage("presetDialog.presetNameTitle"),
		                   JOptionPane.QUESTION_MESSAGE,
		                   IconUtils.getQuestionIcon(),
		                   null,
		                   name
		           );

		return s;

	}

}
