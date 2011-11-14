/* ProfilePathDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.signalml.app.config.GeneralConfiguration;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.app.view.element.ProfilePathTypePanel;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/**
 * Dialog which is displayed at the first use of the application (or if
 * profile directory was deleted). Allows the user to select the directory
 * which should be a profile directory.
 * Contains only one panel - {@link ProfilePathTypePanel}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ProfilePathDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link ProfilePathTypePanel panel} with the actual contents of this
	 * dialog
	 */
	private ProfilePathTypePanel panel;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ProfilePathDialog( Window f, boolean isModal) {
		super( f, isModal);
	}

	/**
	 * Sets the title of this dialog and calls the {@link AbstractDialog#initialize()
	 * initialization} in parent.
	 */
	@Override
	protected void initialize() {
		setTitle(_("Choose profile path"));
		setResizable(false);
		super.initialize();
	}

	/**
	 * Creates the interface for this dialog which contains only one panel -
	 * {@link ProfilePathTypePanel}.
	 */
	@Override
	public JComponent createInterface() {

		panel = new ProfilePathTypePanel();
		return panel;

	}

	/**
	 * Fills the fields of this dialog using the given {@link
	 * GeneralConfiguration model}:
	 * <ul>
	 * <li>if by default the default profile directory should be chosen sets
	 * the {@link ProfilePathTypePanel#getDefaultRadio() default button} as
	 * selected, otherwise sets the {@link ProfilePathTypePanel#getCustomRadio()
	 * custom button} as selected,</li>
	 * <li>if the {@link GeneralConfiguration#getProfilePath() path} to the
	 * profile directory exists:
	 * <ul><li>if the file (directory) exists sets in the file chooser,</li>
	 * <li>if the parent directory exists sets it in the file chooser,</li>
	 * </ul></li>
	 * <li>otherwise sets the user's home directory in the file chooser.</li>
	 * </ul>
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		GeneralConfiguration config = (GeneralConfiguration) model;
		if (config.isProfileDefault()) {
			panel.getDefaultRadio().setSelected(true);
		} else {
			panel.getCustomRadio().setSelected(true);
		}
		String path = config.getProfilePath();
		File file = null;
		if (path != null) {
			file = new File(path);
		}

		boolean fileConfigured = false;

		JFileChooser fileChooser = panel.getFileChooser();

		if (file != null) {
			if (file.exists()) {
				logger.debug("Existing file [" + file.getAbsolutePath() + "]");
				fileChooser.setSelectedFile(file);
				fileConfigured = true;
			} else {
				File parent = file.getParentFile();
				if (parent != null && parent.exists()) {
					logger.debug("Set directory to [" + parent.getAbsolutePath() + "]");
					fileChooser.setCurrentDirectory(parent);
					fileConfigured = true;
				}
			}
		}

		if (!fileConfigured) {
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		}
	}

	/**
	 * Sets in the {@link GeneralConfiguration model} if the {@link
	 * GeneralConfiguration#setProfileDefault(boolean) default directory}
	 * should be used and if not, sets {@link
	 * GeneralConfiguration#setProfilePath(String)} path to it.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		GeneralConfiguration config = (GeneralConfiguration) model;
		boolean def = panel.getDefaultRadio().isSelected();
		config.setProfileDefault(def);
		if (!def) {
			File file = panel.getFileChooser().getSelectedFile();
			logger.debug("Chosen file [" + file.getAbsolutePath() + "]");
			config.setProfilePath(file.getAbsolutePath());
		} else {
			config.setProfilePath(null);
		}
	}

	/**
	 * Validates this dialog. This dialog is valid if either {@link
	 * ProfilePathTypePanel#getDefaultRadio() default button} is selected or
	 * the profile directory is selected in the file chooser (and it is a
	 * directory).
	 */
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		if (!panel.getDefaultRadio().isSelected()) {
			EmbeddedFileChooser fileChooser = panel.getFileChooser();

			fileChooser.forceApproveSelection();
			File f = fileChooser.getSelectedFile();

			if (f == null) {
				logger.debug("Profile path not set");
				errors.rejectValue("profilePath", "error.profilePathMustBeSet");
			} else if (f.exists() && !f.isDirectory()) {
				logger.debug("Profile path is not to a directory");
				errors.rejectValue("profilePath", "error.profilePathMustBeADirectory");
			}
		}
	}

	/**
	 * The model for this class must be of type {@link GeneralConfiguration}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return GeneralConfiguration.class.isAssignableFrom(clazz);
	}

}
