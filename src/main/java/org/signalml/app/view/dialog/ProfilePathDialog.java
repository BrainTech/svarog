/* ProfilePathDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.signalml.app.config.GeneralConfiguration;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.app.view.element.ProfilePathTypePanel;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ProfilePathDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ProfilePathDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ProfilePathTypePanel panel;

	public ProfilePathDialog(MessageSourceAccessor messageSource, Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("profilePath.title"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		panel = new ProfilePathTypePanel(messageSource);
		return panel;

	}

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

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return GeneralConfiguration.class.isAssignableFrom(clazz);
	}

}
