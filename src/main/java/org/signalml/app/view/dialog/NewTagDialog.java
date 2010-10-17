/* NewTagDialog.java created 2007-10-14
 *
 */
package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.NewTagDescriptor;
import org.signalml.app.model.NewTagDescriptor.NewTagTypeMode;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.app.view.element.NewTagPanel;
import org.signalml.app.view.element.PagingParametersPanel;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** NewTagDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private NewTagPanel newTagPanel;
	private PagingParametersPanel pagingParametersPanel;
	private ApplicationConfiguration applicationConfig;

	public NewTagDialog(MessageSourceAccessor messageSource, Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("newTag.title"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		newTagPanel = new NewTagPanel(messageSource);
		pagingParametersPanel = new PagingParametersPanel(messageSource);

		interfacePanel.add(newTagPanel, BorderLayout.CENTER);
		interfacePanel.add(pagingParametersPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		NewTagDescriptor descriptor = (NewTagDescriptor) model;

		NewTagTypeMode mode = descriptor.getMode();
		if (mode == NewTagTypeMode.EMPTY) {
			newTagPanel.getEmptyRadio().setSelected(true);
		}
		else if (mode == NewTagTypeMode.DEFAULT_SLEEP) {
			newTagPanel.getDefaultSleepRadio().setSelected(true);
		}
		else if (mode == NewTagTypeMode.FROM_FILE) {
			newTagPanel.getFromFileRadio().setSelected(true);
		} else {
			throw new SanityCheckException("Unknown mode [" + mode + "]");
		}

		File file = descriptor.getFile();
		if (file != null && file.exists()) {
			newTagPanel.getFileChooser().setSelectedFile(file);
		} else {
			String lastPath = applicationConfig.getLastOpenTagPath();
			if (lastPath == null) {
				lastPath = System.getProperty("user.dir");
			}
			newTagPanel.getFileChooser().setCurrentDirectory(new File(lastPath));
		}

		pagingParametersPanel.fillPanelFromModel(descriptor);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		NewTagDescriptor descriptor = (NewTagDescriptor) model;

		if (newTagPanel.getEmptyRadio().isSelected()) {
			descriptor.setMode(NewTagTypeMode.EMPTY);
			descriptor.setFile(null);
		}
		else if (newTagPanel.getDefaultSleepRadio().isSelected()) {
			descriptor.setMode(NewTagTypeMode.DEFAULT_SLEEP);
			descriptor.setFile(null);
		}
		else if (newTagPanel.getFromFileRadio().isSelected()) {
			descriptor.setMode(NewTagTypeMode.FROM_FILE);
			descriptor.setFile(newTagPanel.getFileChooser().getSelectedFile());
		}

		pagingParametersPanel.fillModelFromPanel(descriptor);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		if (newTagPanel.getFromFileRadio().isSelected()) {

			EmbeddedFileChooser fileChooser = newTagPanel.getFileChooser();

			fileChooser.forceApproveSelection();
			fileChooser.validateFile(errors, "file", false, false, false, false, true);

		}

		pagingParametersPanel.validatePanel(errors);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewTagDescriptor.class.isAssignableFrom(clazz);
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}
