/* ChooseFilesForMonitorRecordingPanel.java created 2010-11-03
 *
 */
package org.signalml.app.view.document.monitor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.view.components.FileSelectPanel;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.springframework.validation.Errors;

/**
 * Represents a panel for selecting files used to record monitor.
 *
 * @author Piotr Szachewicz
 */
public class ChooseFilesForMonitorRecordingPanel extends JPanel {

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
	 * A panel containing a {@link JCheckBox} allowing to enable/disable
	 * the recording of tags (only signal is recorded then).
	 */
	private EnableTagRecordingPanel enableTagRecordingPanel;

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
		setLayout(new GridLayout(2, 1, 2, 2));
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Choose files to which signal and tags will be recorded")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);
		add(getSelectSignalRecordingFilePanel());

		JPanel tagsRecordingPanel = new JPanel(new BorderLayout());
		tagsRecordingPanel.add(getEnableTagRecordingPanel(), BorderLayout.WEST);
		tagsRecordingPanel.add(getSelectTagsRecordingFilePanel(), BorderLayout.CENTER);

		add(tagsRecordingPanel);
	}

	/**
	 * Returns a {@link FileSelectPanel} allowing to select a signal recording
	 * target file.
	 * @return a {@link FileSelectPanel} for selecting signal recording target
	 * file using this panel
	 */
	protected FileSelectPanel getSelectSignalRecordingFilePanel() {
		if (selectSignalRecordingFilePanel == null) {
			selectSignalRecordingFilePanel = new FileSelectPanel( _("Record signal to file"));
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
			selectTagsRecordingFilePanel = new FileSelectPanel( _("Record tags to file"));
			selectTagsRecordingFilePanel.setEnabled(false);
		}
		return selectTagsRecordingFilePanel;
	}

	/**
	 * Returns a {@link Panel} containing a {@link JCheckBox} allowing to
	 * enable/disable tag recording (if tag recording is disabled, then only
	 * signal is recorded).
	 * @return a {@link Panel} for enabling/disabling tag recording
	 */
	protected EnableTagRecordingPanel getEnableTagRecordingPanel() {
		if (enableTagRecordingPanel == null) {
			enableTagRecordingPanel = new EnableTagRecordingPanel();
		}
		return enableTagRecordingPanel;
	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param model the model to be filled.
	 */
	public void fillModelFromPanel(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = ((OpenMonitorDescriptor) model).getMonitorRecordingDescriptor();
		monitorRecordingDescriptor.setSignalRecordingFilePath(getSelectSignalRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingFilePath(getSelectTagsRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingEnabled(getEnableTagRecordingPanel().isTagRecordingEnabled());
	}

	/**
	 * Sets this panel to be enabled or disabled.
	 * @param enabled false to disable this panel, true otherwise
	 */
	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);

		getSelectSignalRecordingFilePanel().setEnabled(enabled);
		if (!enabled) {
			getSelectTagsRecordingFilePanel().setEnabled(false);
			getEnableTagRecordingPanel().setEnabled(false);
		}
		else {
			getEnableTagRecordingPanel().setEnabled(true);
			getSelectTagsRecordingFilePanel().setEnabled(getEnableTagRecordingPanel().isTagRecordingEnabled());
		}

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
		String tagRecordingFileName = getSelectTagsRecordingFilePanel().getFileName();

		if (recordingFileName.isEmpty()) {
			errors.addError(_("Please input a correct signal filename"));
		}
		else if ((new File(recordingFileName)).exists()) {
			int anwser = JOptionPane.showConfirmDialog(null, _("Signal recording target file already exists! Do you want to overwrite?"));
			if (anwser == JOptionPane.CANCEL_OPTION || anwser == JOptionPane.NO_OPTION)
				errors.addError("");
		}

		if (getEnableTagRecordingPanel().isTagRecordingEnabled() && tagRecordingFileName.isEmpty()) {
			errors.addError(_("Please input a correct tag filename"));
		}
		else if (getEnableTagRecordingPanel().isTagRecordingEnabled() && (new File(tagRecordingFileName)).exists()) {
			int anwser = JOptionPane.showConfirmDialog(null,
					    _("Tag recording target file already exists! Do you want to overwrite?"));
			if (anwser == JOptionPane.CANCEL_OPTION || anwser == JOptionPane.NO_OPTION)
				errors.addError("");
		}

	}

	/**
	 * Represents a panel that contains a checkbox to enable/disable tag recording.
	 */
	protected class EnableTagRecordingPanel extends JPanel {

		private JCheckBox enableTagRecordingCheckBox = null;

		/**
		 * Constructor. Creates a new {@link DisableTagRecordingPanel}.
		 */
		public EnableTagRecordingPanel() {
		
			enableTagRecordingCheckBox = new JCheckBox();
			enableTagRecordingCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						getSelectTagsRecordingFilePanel().setEnabled(true);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						getSelectTagsRecordingFilePanel().setEnabled(false);
					}
				}
			});

			add(enableTagRecordingCheckBox);
			//add(new JLabel(_("Do not record tags")));
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
		 * Returns whether tag recording was disabled using this panel.
		 * @return true if tag recording was disabled, false otherwise
		 */
		public boolean isTagRecordingDisabled() {
			return !isTagRecordingEnabled();
		}

		/**
		 * Returns whether tag recording was enabled using this panel.
		 * @return true if tag recording was enabled, false otherwise
		 */
		public boolean isTagRecordingEnabled() {
			return enableTagRecordingCheckBox.isSelected();
		}

		/**
		 * Sets the status of the tag recording checkbox.
		 * @param enable true to disable tag recording, false otherwise
		 */
		public void setTagRecordingEnabled(boolean enable) {
			enableTagRecordingCheckBox.setSelected(enable);
		}
	}

}
