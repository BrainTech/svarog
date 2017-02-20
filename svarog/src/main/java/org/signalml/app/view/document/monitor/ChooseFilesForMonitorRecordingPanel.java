/* ChooseFilesForMonitorRecordingPanel.java created 2010-11-03
 *
 */
package org.signalml.app.view.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.view.common.components.filechooser.FileSelectPanel;

/**
 * Represents a panel for selecting files used to record monitor.
 *
 * @author Piotr Szachewicz
 */
public class ChooseFilesForMonitorRecordingPanel extends JPanel implements DocumentListener {

	/**
	 * A {@link FileSelectPanel} for selecting a signal recording target file
	 * for a monitor recording.
	 */
	private FileSelectPanel selectSignalRecordingFilePanel;

	/**
	 * A {@link FileSelectPanel} for selecting a tag recording target file
	 * for a monitor recording.
	 */
	private FileSelectPanel selectTagsRecordingFilePanel;

	/**
	 * A {@link FileSelectPanel} for selecting a video recording target file
	 * for a monitor recording.
	 */
	private FileSelectPanel selectVideoRecordingFilePanel;

	/**
	 * A panel containing a {@link JCheckBox} allowing to enable/disable
	 * the recording of tags (only signal (and, optionally, video) is recorded then).
	 */
	private EnableRecordingPanel enableTagRecordingPanel;

	/**
	 * A panel containing a {@link JCheckBox} allowing to enable/disable
	 * the recording of video (only signal (and, optionally, tags) is recorded then).
	 */
	private EnableRecordingPanel enableVideoRecordingPanel;

	/**
	 * Constructor.
	 *
	 * localized message codes
	 */
	public ChooseFilesForMonitorRecordingPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	private void initialize() {
		setLayout(new GridLayout(3, 1, 2, 2));
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Choose files to which signal and tags will be recorded")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);
		add(getSelectSignalRecordingFilePanel());

		JPanel tagsRecordingPanel = new JPanel(new BorderLayout());
		tagsRecordingPanel.add(getEnableTagRecordingPanel(), BorderLayout.WEST);
		tagsRecordingPanel.add(getSelectTagsRecordingFilePanel(), BorderLayout.CENTER);

		add(tagsRecordingPanel);

		JPanel videoRecordingPanel = new JPanel(new BorderLayout());
		videoRecordingPanel.add(getEnableVideoRecordingPanel(), BorderLayout.WEST);
		videoRecordingPanel.add(getSelectVideoRecordingFilePanel(), BorderLayout.CENTER);

		add(videoRecordingPanel);
	}

	/**
	 * Returns a {@link FileSelectPanel} allowing to select a signal recording
	 * target file.
	 * @return a {@link FileSelectPanel} for selecting signal recording target
	 * file using this panel
	 */
	protected FileSelectPanel getSelectSignalRecordingFilePanel() {
		if (selectSignalRecordingFilePanel == null) {
			selectSignalRecordingFilePanel = new FileSelectPanel(_("Record signal to file"));
			selectSignalRecordingFilePanel.getFileNameField().getDocument().addDocumentListener(this);
		}
		return selectSignalRecordingFilePanel;
	}

	/**
	 * Returns a {@link FileSelectPanel} allowing to select a tags recording
	 * target file.
	 * @return a {@link FileSelectPanel} for selecting tags recording target
	 * file using this panel
	 */
	protected FileSelectPanel getSelectTagsRecordingFilePanel() {
		if (selectTagsRecordingFilePanel == null) {
			selectTagsRecordingFilePanel = new FileSelectPanel(_("Record tags to file"));
			selectTagsRecordingFilePanel.setEnabled(false);
		}
		return selectTagsRecordingFilePanel;
	}

	/**
	 * Returns a {@link FileSelectPanel} allowing to select a video recording
	 * target file.
	 * @return a {@link FileSelectPanel} for selecting video recording target
	 * file using this panel
	 */
	protected FileSelectPanel getSelectVideoRecordingFilePanel() {
		if (selectVideoRecordingFilePanel == null) {
			selectVideoRecordingFilePanel = new FileSelectPanel(_("Record video to file"));
			selectVideoRecordingFilePanel.setEnabled(false);
		}
		return selectVideoRecordingFilePanel;
	}

	/**
	 * Returns a {@link Panel} containing a {@link JCheckBox} allowing to
	 * enable/disable tag recording (if tag recording is disabled, then only
	 * signal is recorded).
	 * @return a {@link Panel} for enabling/disabling tag recording
	 */
	protected EnableRecordingPanel getEnableTagRecordingPanel() {
		if (enableTagRecordingPanel == null) {
			enableTagRecordingPanel = new EnableRecordingPanel(getSelectTagsRecordingFilePanel());
		}
		return enableTagRecordingPanel;
	}

	/**
	 * Returns a {@link Panel} containing a {@link JCheckBox} allowing to
	 * enable/disable video recording.
	 * @return a {@link Panel} for enabling/disabling video recording
	 */
	protected EnableRecordingPanel getEnableVideoRecordingPanel() {
		if (enableVideoRecordingPanel == null) {
			enableVideoRecordingPanel = new EnableRecordingPanel(getSelectVideoRecordingFilePanel());
		}
		return enableVideoRecordingPanel;
	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param model the model to be filled.
	 */
	public void fillModelFromPanel(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = ((ExperimentDescriptor) model).getMonitorRecordingDescriptor();
		monitorRecordingDescriptor.setSignalRecordingFilePath(getSelectSignalRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingFilePath(getSelectTagsRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingEnabled(getEnableTagRecordingPanel().isRecordingEnabled());
		monitorRecordingDescriptor.setVideoRecordingFilePath(getSelectVideoRecordingFilePanel().getFileName());
	}


	public void fillPanelFromModel(Object model) {
		ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) model;
		MonitorRecordingDescriptor monitorRecordingDescriptor = experimentDescriptor.getMonitorRecordingDescriptor();
		getEnableTagRecordingPanel().setRecordingEnabled(monitorRecordingDescriptor.isTagsRecordingEnabled());
		getEnableVideoRecordingPanel().setRecordingEnabled(monitorRecordingDescriptor.isVideoRecordingEnabled());
		getEnableVideoRecordingPanel().setEnabled(experimentDescriptor.getHasVideoSaver());
		getSelectSignalRecordingFilePanel().setFileName(monitorRecordingDescriptor.getSignalRecordingFilePath());
		getSelectTagsRecordingFilePanel().setFileName(monitorRecordingDescriptor.getTagsRecordingFilePath());
		getSelectVideoRecordingFilePanel().setFileName(monitorRecordingDescriptor.getVideoRecordingFilePath());
	}

	/**
	 * Sets this panel to be enabled or disabled.
	 * @param enabled false to disable this panel, true otherwise
	 */
	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);

		getSelectSignalRecordingFilePanel().setEnabled(enabled);
		getEnableTagRecordingPanel().setEnabled(enabled);
		getSelectTagsRecordingFilePanel().setEnabled(enabled && getEnableTagRecordingPanel().isRecordingEnabled());
		getEnableVideoRecordingPanel().setEnabled(enabled);
		getSelectVideoRecordingFilePanel().setEnabled(enabled && getEnableVideoRecordingPanel().isRecordingEnabled());
	}

	/**
	 * Resets the signal and tag recording filenames to empty strings.
	 */
	public void resetFileNames() {
		getSelectSignalRecordingFilePanel().setFileName("");
		getSelectTagsRecordingFilePanel().setFileName("");
	}

	/**
	 * Checks if this panel is properly filled.
	 * @param model the model for this panel
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Object model, ValidationErrors errors) {

		String recordingFileName = getSelectSignalRecordingFilePanel().getFileName();
		validateRecordingFileName("Signal", recordingFileName, errors);

		if (getEnableTagRecordingPanel().isRecordingEnabled()) {
			String tagRecordingFileName = getSelectTagsRecordingFilePanel().getFileName();
			validateRecordingFileName("Tag", tagRecordingFileName, errors);
		}
		if (getEnableVideoRecordingPanel().isRecordingEnabled()) {
			String videoRecordingFileName = getSelectVideoRecordingFilePanel().getFileName();
			validateRecordingFileName("Video", videoRecordingFileName, errors);
		}
	}

	private static void validateRecordingFileName(String type, String recordingFileName, ValidationErrors errors) {
		if (recordingFileName.isEmpty()) {
			errors.addError(_("Please input a correct "+type.toLowerCase()+" filename"));
		}
		else if ((new File(recordingFileName)).exists()) {
			int answer = JOptionPane.showConfirmDialog(null,
						 _(type + " recording target file already exists! Do you want to overwrite?"));
			if (answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.NO_OPTION)
				errors.addError("");
		}
	}

	/**
	 * Represents a panel that contains a checkbox
	 * to enable/disable recording of tags or video.
	 */
	protected static class EnableRecordingPanel extends JPanel {

		private JCheckBox enableRecordingCheckBox = null;

		/**
		 * Constructor. Creates a new {@link EnableRecordingPanel}.
		 * @param fileSelectPanel  file name field associated with the checkbox
		 */
		public EnableRecordingPanel(FileSelectPanel fileSelectPanel) {
			enableRecordingCheckBox = new JCheckBox();
			attachComponentToToggle(fileSelectPanel);
			add(enableRecordingCheckBox);
		}

		/**
		 * Connect this EnableRecordingPanel to a component.
		 *
		 * Every time this panel will have its checkbox checked,
		 * the connected component will be enabled.
		 * Every time the checkbox is unchecked, the connected component
		 * will be disabled.
		 *
		 * @param component  component to be attached
		 */
		public void attachComponentToToggle(Component component) {
			enableRecordingCheckBox.addItemListener((ItemEvent e) -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					component.setEnabled(true);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					component.setEnabled(false);
				}
			});
		}

		/**
		* Sets this panel to be enabled or disabled.
		* @param enabled false to disable this panel, true otherwise
		*/
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);

			for (Component component : this.getComponents()) {
				component.setEnabled(enabled);
			}
		}

		/**
		 * Returns whether recording was enabled using this panel.
		 * @return true if recording was enabled, false otherwise
		 */
		public boolean isRecordingEnabled() {
			return enableRecordingCheckBox.isSelected();
		}

		/**
		 * Sets the status of the recording checkbox.
		 * @param enable true to disable tag recording, false otherwise
		 */
		public void setRecordingEnabled(boolean enable) {
			enableRecordingCheckBox.setSelected(enable);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateNamesToFitSignalName();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateNamesToFitSignalName();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateNamesToFitSignalName();
	}

	public void updateNamesToFitSignalName() {
		String fileName = getSelectSignalRecordingFilePanel().getFileName();
		getSelectTagsRecordingFilePanel().setFileName(fileName);
		getSelectVideoRecordingFilePanel().setFileName(fileName);
	}

}
