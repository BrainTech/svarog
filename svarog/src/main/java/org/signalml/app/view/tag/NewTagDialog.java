/* NewTagDialog.java created 2007-10-14
 *
 */
package org.signalml.app.view.tag;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.config.preset.managers.StyledTagSetPresetManager;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.tag.NewTagDescriptor;
import org.signalml.app.model.tag.NewTagDescriptor.NewTagTypeMode;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.filechooser.EmbeddedFileChooser;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.signal.PagingParametersPanel;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.validation.Errors;

/**
 * Dialog which allows to create the new {@link TagDocument tag document}.
 * Contains two sub-panels to choose the parameters of the document:
 * <ul>
 * <li>the {@link NewTagPanel panel} which allows to select which {@link
 * TagStyle styles} should be located in the new {@link TagDocument tag
 * document},</li>
 * <li>the {@link PagingParametersPanel panel} which allows to select the
 * size of the block and the page of the signal that should be set in the
 * new tag document.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link NewTagPanel panel} which allows to select which {@link
	 * TagStyle styles} should be located in the new {@link TagDocument tag
	 * document}
	 */
	private NewTagPanel newTagPanel;
	/**
	 * the {@link ApplicationConfiguration configuration} of Svarog
	 */
	private ApplicationConfiguration applicationConfig;

	/**
	 * {@link PresetManager} for handling tag styles presets.
	 */
	private final StyledTagSetPresetManager styledTagSetPresetManager;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param styledTagSetPresetManager {@link PresetManager} handling tag style presets
	 * @param f the parent window or null if there is no parent
	 * @param isModal dialog blocks top-level windows if true
	 */
	public NewTagDialog(Window f, boolean isModal) {
		super(f, isModal);
		this.styledTagSetPresetManager = SvarogApplication.getManagerOfPresetsManagers().getStyledTagSetPresetManager();
	}

	/**
	 * Sets the title and calls the {@link AbstractDialog#initialize()
	 * initialization} in {@link AbstractDialog parent}.
	 */
	@Override
	protected void initialize() {
		setTitle(_("Choose new tag type"));
		setResizable(false);
		super.initialize();
	}

	/**
	 * Creates the interface for this panel with BorderLayout and two
	 * sub-panels:
	 * <ul>
	 * <li>the {@link NewTagPanel panel} which allows to select which {@link
	 * TagStyle styles} should be located in the new {@link TagDocument tag
	 * document},</li>
	 * <li>the {@link PagingParametersPanel panel} which allows to select the
	 * size of the block and the page of the signal that should be set in the
	 * new tag document.</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		newTagPanel = new NewTagPanel(styledTagSetPresetManager);

		interfacePanel.add(newTagPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	/**
	 * Using the given {@link NewTagDescriptor model}:
	 * <ul>
	 * <li>depending on the {@link NewTagTypeMode mode} sets the selected
	 * radio button,</li>
	 * <li>sets in the {@link NewTagPanel#getFileChooser() file chooser} the
	 * selected file or if there is none the path,</li>
	 * <li>{@link PagingParametersPanel#fillPanelFromModel(
	 * org.signalml.app.model.PagingParameterDescriptor) fills} the {@link
	 * PagingParametersPanel}.</li></ul>
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		NewTagDescriptor descriptor = (NewTagDescriptor) model;

		NewTagTypeMode mode = descriptor.getMode();
		if (mode == NewTagTypeMode.EMPTY) {
			newTagPanel.getEmptyRadio().setSelected(true);
		}
		else if (mode == NewTagTypeMode.DEFAULT_PRESET)
		{
			newTagPanel.getDefaultPresetsRadio().setSelected(true);
		}
		else if (mode == NewTagTypeMode.PRESET) {
			newTagPanel.getPresetRadio().setSelected(true);
		}
		else if (mode == NewTagTypeMode.FROM_FILE) {
			newTagPanel.getFromFileRadio().setSelected(true);
		} else {
			throw new SanityCheckException("Unknown mode [" + mode + "]");
		}

		newTagPanel.getPresetRadio().setEnabled(styledTagSetPresetManager.getPresetCount() > 0);

		File file = descriptor.getFile();
		if (file != null && file.exists()) {
			newTagPanel.getFileChooser().setSelectedFile(file);
		} else {
			String lastPath = applicationConfig.getLastFileChooserPath();
			newTagPanel.getFileChooser().setCurrentDirectory(new File(lastPath));
		}
	}

	/**
	 * Using the user input fills the given {@link NewTagDescriptor model}:
	 * <ul>
	 * <li>depending on the selected radio button
	 * {@link NewTagDescriptor#setMode(NewTagTypeMode) sets} the
	 * {@link NewTagTypeMode mode},</li>
	 * <li>sets the file - if {@link NewTagPanel#getFromFileRadio() from file
	 * radio button} is selected using the {@link NewTagPanel#getFileChooser()
	 * file chooser} otherwise {@code null},</li>
	 * <li>{@link PagingParametersPanel#fillModelFromPanel(
	 * org.signalml.app.model.PagingParameterDescriptor) fills} it from the
	 * {@link PagingParametersPanel}.</li></ul>
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		NewTagDescriptor descriptor = (NewTagDescriptor) model;

		if (newTagPanel.getEmptyRadio().isSelected()) {
			descriptor.setMode(NewTagTypeMode.EMPTY);
			descriptor.setFile(null);
		}
		else if (newTagPanel.getPresetRadio().isSelected()) {
			descriptor.setMode(NewTagTypeMode.PRESET);
			descriptor.setTagStylesPreset((StyledTagSet) newTagPanel.getPresetComboBox().getSelectedItem());
		}
		else if (newTagPanel.getDefaultPresetsRadio().isSelected()){
			descriptor.setMode(NewTagTypeMode.DEFAULT_PRESET);
			descriptor.setDefaultPresetIndex(newTagPanel.getDefaultPresetComboBox().getSelectedIndex());
		}
		else if (newTagPanel.getFromFileRadio().isSelected()) {
			descriptor.setMode(NewTagTypeMode.FROM_FILE);
			descriptor.setFile(newTagPanel.getFileChooser().getSelectedFile());
		}
	}

	/**
	 * Validates this dialog.
	 * This dialog is valid if:
	 * <ul>
	 * <li>the {@link PagingParametersPanel} is
	 * {@link PagingParametersPanel#validatePanel(Errors) valid} and</li>
	 * <li>if the {@link NewTagPanel#getFromFileRadio() from file radio button}
	 * is selected - if the file is valid.</li></ul>
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {

		if (newTagPanel.getFromFileRadio().isSelected()) {

			EmbeddedFileChooser fileChooser = newTagPanel.getFileChooser();
			fileChooser.validateFile(errors, "file", false, false, false, false, true);

		}
		else if (newTagPanel.getPresetRadio().isSelected()) {
			if (newTagPanel.getPresetComboBox().getSelectedItem() == null)
				errors.addError(_("Please select a preset!"));
		}
	}

	/**
	 * The model for this dialog must be of type {@link NewTagDescriptor}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewTagDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	/**
	 * Sets the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}
