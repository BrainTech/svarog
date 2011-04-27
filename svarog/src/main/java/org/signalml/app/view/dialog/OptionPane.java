/* OptionPane.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Component;
import java.io.File;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.MutableDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.PrettyStringLocaleWrapper;
import org.signalml.domain.montage.Montage;
import org.signalml.method.SerializableMethod;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class contains different static methods which show simple dialogs
 * (mostly informational or YES/NO).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OptionPane extends JOptionPane {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OptionPane.class);

	/**
	 * the boolean which says if the static variables have been initialized
	 */
	private static boolean initialized = false;

	/**
	 * the static source of messages (labels)
	 */
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

	/**
	 * Returns the source of messages (labels).
	 * @return the source of messages (labels)
	 */
	public static MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	/**
	 * Sets the source of messages (labels).
	 * @param messageSource the source of messages (labels)
	 */
	public static void setMessageSource(MessageSourceAccessor messageSource) {
		OptionPane.messageSource = messageSource;
	}

	/**
	 * Obtains the messages from the source of messages and sets them to
	 * the static variables.
	 */
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

	/**
	 * Shows the dialog with the description of an error.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param message the code of the message that should be displayed in the
	 * source of messages
	 */
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

	/**
	 * Shows the dialog with the description of an error.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param message the code of the message that should be displayed in the
	 * source of messages
	 * @param arguments the codes of arguments used to obtain the message from
	 * the source of messages
	 */
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

	/**
	 * Shows the dialog with the description of an error.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param message the message which describes the error
	 */
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

	/**
	 * Shows the dialog with the specified message.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param message the code of the message that should be displayed in the
	 * source of messages
	 */
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

	/**
	 * Shows the dialog with the specified message.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param message the message which describes the error
	 */
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

	/**
	 * Shows the dialog with the description of an exception.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param message the code of the message that should be displayed in the
	 * source of messages
	 * @param ex the exception which description should be displayed; used to
	 * obtain the message that should be displayed
	 */
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

	/**
	 * Shows the information that the document is already opened and asks the
	 * user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to reload the file,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel opening,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the running tasks will be aborted and asks
	 * the user what to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showTaskRunning(Component parent) {
		return showProceedOption(parent, "situation.tasksWillBeAborted");
	}

	/**
	 * Shows the information that the artifact project already exits and asks
	 * the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to create the project,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Informs that the artifact project exists and asks the user whether to
	 * reuse it or to replace it.
	 * @param parent the window parent to this one
	 * @param name the name of the project
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to reuse the project,</li>
	 * <li>{@code N0_OPTION} if the user chose to replace the project,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showArtifactProjectExists(Component parent, String name) {
		return showReuseReplaceOption(parent, "artifactMethod.situation.artifactProjectExists", new Object[] { name });
	}

	/**
	 * Informs that the stager project exists and asks the user whether to
	 * reuse it or to replace it.
	 * @param parent the window parent to this one
	 * @param name the name of the project
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to reuse the project,</li>
	 * <li>{@code N0_OPTION} if the user chose to replace the project,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showStagerProjectExists(Component parent, String name) {
		return showReuseReplaceOption(parent, "stagerMethod.situation.stagerProjectExists", new Object[] { name });
	}

	/**
	 * Shows the information that the directory doesn't exist and asks the
	 * user what to do. 
	 * @param parent the window parent to this one
	 * @param file the directory that doesn't exist
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to create the directory,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the document has not been saved and asks the
	 * user what to do. 
	 * @param parent the window parent to this one
	 * @param document the document that was not saved
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to save the document,</li>
	 * <li>{@code NO_OPTION} if the user chose not to save the document,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel closing the
	 * document,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the exported signal has to be normalized
	 * and asks the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to normalize the signal,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the export,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Asks the user if the specified task should be continued.
	 * @param parent the window parent to this one
	 * @param messageCode the code used to obtain the message describing the
	 * task from the source of messages
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Asks the user if the specified task should be continued.
	 * @param parent the window parent to this one
	 * @param messageCode the code used to obtain the message describing the
	 * task from the source of messages
	 * @param args the codes of arguments used to obtain the message from
	 * the source of messages
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Informs that the object exists and asks the user whether to reuse it or
	 * to replace it.
	 * @param parent the window parent to this one
	 * @param messageCode the code used to obtain the message for the dialog
	 * from the source of messages
	 * @param args the codes of arguments used to obtain the message from
	 * the source of messages
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to reuse the object,</li>
	 * <li>{@code N0_OPTION} if the user chose to replace the object,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Informs that the {@link SerializableMethod serializable tasks} are
	 * running and asks the user whether to suspend them or to abort them.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to suspends the tasks,</li>
	 * <li>{@code N0_OPTION} if the user chose to abort the tasks,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Informs that the changes to the raw configuration will be lost and asks
	 * the user what to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showRawConfigWillBeLost(Component parent) {
		return showProceedOption(parent, "situation.rawConfigWillBeLost");
	}

	/**
	 * Informs that all running tasks will be aborted and asks the user what to
	 * do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showAbortAllTasks(Component parent) {
		return showProceedOption(parent, "situation.abortAllTasks");
	}

	/**
	 * Informs that all running tasks (that can be suspended) will be suspended
	 * and asks the user what to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showSuspendAllTasks(Component parent) {
		return showProceedOption(parent, "situation.suspendAllTasks");
	}

	/**
	 * Informs that all suspended tasks will be resumed and asks the user what
	 * to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to proceed,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showResumeAllTasks(Component parent) {
		return showProceedOption(parent, "situation.resumeAllTasks");
	}

	/**
	 * Informs that all non running tasks will be removed and asks the user
	 * what to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to remove them,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showRemoveAllTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllTasks");
	}

	/**
	 * Informs that all finished tasks will be removed and asks the user what
	 * to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to remove them,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showRemoveAllFinishedTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllFinishedTasks");
	}

	/**
	 * Informs that all aborted tasks will be removed and asks the user what
	 * to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to remove them,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showRemoveAllAbortedTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllAbortedTasks");
	}

	/**
	 * Informs that all failed tasks will be removed and asks the user what
	 * to do.
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to remove them,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
	public static int showRemoveAllFailedTasks(Component parent) {
		return showProceedOption(parent, "situation.removeAllFailedTasks");
	}

	/**
	 * Shows the information that the file already exits and asks
	 * the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to overwrite it,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the file already exits and asks
	 * the user what to do. 
	 * @param parent the window parent to this one
	 * @param fileName the name of the file
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to overwrite it,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that there are tasks that are restored from the
	 * previous run and asks the user if they should be restored. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to restore them,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose not to restore them,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the {@link TagDocument tag document} is not
	 * compatible with the signal and asks the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to continue,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the {@link TagDocument tag document} has
	 * different checksum than the signal and asks the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code OK_OPTION} if the user chose to continue,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that there are other document dependent on the
	 * closed one and asks the user whether to close them. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to close,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the document of the specified path is already
	 * open and the user should choose a different path.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that the selected file is not found.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param file the file that was not found or null if no name should be
	 * used
	 */
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

	/**
	 * Shows the information that the selected directory is not found.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param file the directory that was not found or null if no name should
	 * be used
	 */
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

	/**
	 * Shows the information that the selected directory is not accessible.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param file the directory that was not accessible or null if no name
	 * should be used
	 */
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

	/**
	 * Shows the information that the selected directory could not be created.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 * @param file the directory that was not created or null if no name should
	 * be used
	 */
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

	/**
	 * Shows the information that the default {@link Montage montage} is not
	 * compatible with the signal.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that the {@link Preset preset} is not compatible
	 * with the signal and has to be edited.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that the {@link TagStyle tag style} is used and
	 * can't be removed.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that the {@link TagStyle tag style} has been
	 * modified and asks the user whether to apply changes. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to apply changes,</li>
	 * <li>{@code NO_OPTION} if the user chose discard changes,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the {@link Montage montage} in the {@link
	 * TagDocument tag document} is different from the current montage (when
	 * the document is to be saved) and asks
	 * the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to keep the original montage,
	 * </li>
	 * <li>{@code NO_OPTION} if the user chose to replace the montage with
	 * current,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that the {@link Montage montage} in the {@link
	 * TagDocument tag document} is different from the current montage (when
	 * the document is to be loaded) and asks
	 * the user what to do. 
	 * @param parent the window parent to this one
	 * @return
	 * <ul>
	 * <li>{@code YES_OPTION} if the user chose to load the montage from tag
	 * document,</li>
	 * <li>{@code NO_OPTION} if the user chose to keep the current montage,</li>
	 * <li>{@code CLOSED_OPTION} if the user chose to cancel the operation,</li>
	 * <li>{@code CANCEL_OPTION} if the user canceled the dialog.</li></ul>
	 */
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

	/**
	 * Shows the information that there is no active signal and the user should
	 * choose the tab with the signal before performing the operation.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that there is no active book and the user should
	 * choose the tab with the book before performing the operation.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that there is no active tag and the user should
	 * open a {@link TagDocument tag document} before performing the operation.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the information that there is no signal selection and the user
	 * should select the part of the the signal before performing the operation.
	 * The dialog has only OK option.
	 * @param parent the window parent to this one
	 */
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

	/**
	 * Shows the dialog which allows to choose the language for Svarog from the
	 * list of {@link SvarogConstants#AVAILABLE_LOCALES available locales}.
	 * @return the locale chosen by user or {@code null} if the user canceled
	 * the dialog
	 */
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

	/**
	 * Shows the dialog which allows the user to input the name for the
	 * {@link Preset preset}.
	 * @param parent the window parent to this one
	 * @param name the initial value of the name
	 * @return the input name of the preset
	 */
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
