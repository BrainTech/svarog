/* RegisterCodecDialog.java created 2007-09-18
 *
 */

package org.signalml.app.view.signal.signalml;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.RegisterCodecDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.components.filechooser.EmbeddedFileChooser;
import org.signalml.app.view.common.dialogs.AbstractWizardDialog;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.XMLSignalMLCodec;
import org.signalml.codec.generator.xml.XMLCodecException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

import org.springframework.validation.Errors;


/**
 * {@link AbstractWizardDialog Wizard dialog} to register a new
 * {@link SignalMLCodec codec}.
 * This dialog contains two steps:
 * <ul>
 * <li>{@link RegisterCodecStepOnePanel first}, in which the user selects
 * the the file with the codec,</li>
 * <li>{@link RegisterCodecStepTwoPanel second}, in which the user selects the
 * name for the codec.</li>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecDialog extends AbstractWizardDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalMLCodecManager manager} of {@link SignalMLCodec codecs}
	 */
	private SignalMLCodecManager codecManager;

	/**
	 * the {@link SignalMLCodec codec} read in this dialog
	 */
	private XMLSignalMLCodec currentCodec;

	/**
	 * the profile directory
	 */
	private File profileDir;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param f the parent window or null if there is no parent
	 * @param dialog blocks top-level windows if true
	 */
	public RegisterCodecDialog(Window f, boolean isModal) {
		super(f, isModal);
	}

	/**
	 * Initializes this dialog:
	 * <ul><li>sets the title and icon for this dialog,</li>
	 * <li>adds a listener which {@link #checkNameExists(String) checks} if
	 * the {@link SignalMLCodec codec} of that name exists and displays the
	 * warning,</li>
	 * <li>calls {@link AbstractWizardDialog#initializeNow() initialization}
	 * in parent class.</li></ul>
	 */
	@Override
	protected void initialize() {

		setTitle(_("Register a new SignalML codec"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/fileopen.png"));

		super.initialize();

		getStepTwoPanel().getNameField().getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				react(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				react(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				react(e);
			}

			private void react(DocumentEvent e) {
				try {
					checkNameExists(e.getDocument().getText(0, e.getDocument().getLength()));
				} catch (BadLocationException ex) {
					logger.error("Sanity exception", ex);
				}
			}

		});

	}

	/**
	 * Returns the number of steps, which equals {@code two}
	 */
	@Override
	public int getStepCount() {
		return 2;
	}

	/**
	 * If it is the change from first step to second puts the
	 * {@link XMLSignalMLCodec#getFormatName() name} of the codec in the
	 * {@link RegisterCodecStepTwoPanel#getName() text field} and
	 * {@link #checkNameExists(String) checks} if such name already exists.
	 */
	@Override
	protected boolean onStepChange(int toStep, int fromStep, Object model) {
		if (fromStep == 0) {
			String defaultFormatName = currentCodec.getFormatName();
			getStepTwoPanel().getNameField().setText(defaultFormatName);
			checkNameExists(defaultFormatName);
		}
		return true;
	}

	/**
	 * For step one:
	 * <ul><li>checks if the file is {@link EmbeddedFileChooser#validateFile(
	 * Errors, String, boolean, boolean, boolean, boolean, boolean) valid} and
	 * </li><li>creates the {@link XMLSignalMLCodec codec}</li>
	 * </ul>
	 * For step two:
	 * <ul><li>checks if the name of the codec is valid.</li></ul>
	 */
	@Override
	public void validateDialogStep(int step, Object model, ValidationErrors errors) throws SignalMLException {
		if (step == 0) {

			EmbeddedFileChooser fileChooser = getStepOnePanel().getFileChooser();
			fileChooser.validateFile(errors, "sourceFile", false, false, false, false, true);

			if (!errors.hasErrors()) {
				File file = fileChooser.getSelectedFile();
				try {
					currentCodec = new XMLSignalMLCodec(file, profileDir);
				} catch (IOException ex) {
					logger.debug("Failed to read codec file", ex);
					errors.addError(_("Failed to read file"));
				} catch (XMLCodecException ex) {
					logger.debug("Failed to compile codec file", ex);
					errors.addError(_("Failed to compile the codec"));
				}
			}
		} else if (step == 1) {
			String name = getStepTwoPanel().getNameField().getText();
			if (name == null || name.length() == 0) {
				errors.addError(_("Format name must be set"));
			}
			if (Util.hasSpecialChars(name)) {
				errors.addError(_("Format name must not contain control characters"));
			}
		}
	}

	/**
	 * Checks if there {@link SignalMLCodecManager#getCodecForFormat(String)
	 * is} a {@link SignalMLCodec codec} of a given name.
	 * If there is makes the {@link RegisterCodecStepTwoPanel#getWarningLabel()
	 * warning label} in step two visible, otherwise makes it invisible.
	 * @param formatName the name of the codec
	 */
	private void checkNameExists(String formatName) {
		if (codecManager.getCodecForFormat(formatName) != null) {
			getStepTwoPanel().getWarningLabel().setVisible(true);
		} else {
			getStepTwoPanel().getWarningLabel().setVisible(false);
		}
	}

	/**
	 * Calls the {@link AbstractWizardDialog#resetDialog() reset function} in
	 * parent and makes the {@link RegisterCodecStepTwoPanel#getWarningLabel()
	 * warning label} in step two invisible.
	 */
	@Override
	protected void resetDialog() {
		super.resetDialog();
		getStepTwoPanel().getWarningLabel().setVisible(false);
	}

	/**
	 * Creates the interface for step:
	 * <ul><li>one - {@link RegisterCodecStepOnePanel},</li>
	 * <li>two - {@link RegisterCodecStepTwoPanel},</li></ul>
	 * @throws IndexOutOfBoundsException if the number of the step is
	 * different from 1 or 2
	 */
	@Override
	public JComponent createInterfaceForStep(int step) {
		switch (step) {
		case 0 :
			return new RegisterCodecStepOnePanel();
		case 1 :
			return new RegisterCodecStepTwoPanel();
		default :
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Finish is allowed only for the last (second) step.
	 */
	@Override
	public boolean isFinishAllowedOnStep(int step) {
		return (step == (getStepCount() - 1));
	}

	/**
	 * Returns the {@link RegisterCodecStepOnePanel panel} for the first
	 * step.
	 * @return the panel for the first step
	 */
	public RegisterCodecStepOnePanel getStepOnePanel() {
		return (RegisterCodecStepOnePanel) getInterfaceForStep(0);
	}

	/**
	 * Returns the {@link RegisterCodecStepTwoPanel panel} for the second
	 * step.
	 * @return the panel for the second step
	 */
	public RegisterCodecStepTwoPanel getStepTwoPanel() {
		return (RegisterCodecStepTwoPanel) getInterfaceForStep(1);
	}

	/**
	 *
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		RegisterCodecDescriptor rcd = (RegisterCodecDescriptor) model;
		File f = rcd.getSourceFile();
		if (f == null) {
			f = new File("");
		}
		getStepOnePanel().getFileChooser().setSelectedFile(f);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		RegisterCodecDescriptor rcd = (RegisterCodecDescriptor) model;
		rcd.setSourceFile(getStepOnePanel().getFileChooser().getSelectedFile());
		rcd.setCodec(currentCodec);
		rcd.setFormatName(getStepTwoPanel().getNameField().getText());
	}

	/**
	 * Model have to have type {@link RegisterCodecDescriptor}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return RegisterCodecDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * Returns the {@link SignalMLCodecManager manager} of
	 * {@link SignalMLCodec codecs}.
	 * @return the manager of codecs
	 */
	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	/**
	 * Sets the {@link SignalMLCodecManager manager} of
	 * {@link SignalMLCodec codecs}.
	 * @param codecManager the manager of codecs
	 */
	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	/**
	 * Returns the profile directory.
	 * @return the profile directory
	 */
	public File getProfileDir() {
		return profileDir;
	}

	/**
	 * Sets the profile directory.
	 * @param profileDir the profile directory
	 */
	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

}
