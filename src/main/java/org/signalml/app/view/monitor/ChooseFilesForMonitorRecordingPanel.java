/* ChooseFilesForMonitorRecordingPanel.java created 2010-11-03
 *
 */
package org.signalml.app.view.monitor;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.MonitorRecordingDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.element.FileSelectPanel;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 *
 * @author Piotr Szachewicz
 */
public class ChooseFilesForMonitorRecordingPanel extends JPanel {

	private final MessageSourceAccessor messageSource;
	private FileSelectPanel selectSignalRecordingFilePanel;
	private FileSelectPanel selectTagsRecordingFilePanel;
	private DisableTagRecordingPanel disableTagRecordingPanel;

	public ChooseFilesForMonitorRecordingPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {
		setLayout(new GridLayout(3, 1, 10, 5));
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("startMonitorRecording.chooseFilesTitle")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);
		add(getSelectSignalRecordingFilePanel());
		add(getSelectTagsRecordingFilePanel());
		add(getDisableTagRecordingPanel());
	}

	public FileSelectPanel getSelectSignalRecordingFilePanel() {
		if (selectSignalRecordingFilePanel == null) {
			selectSignalRecordingFilePanel = new FileSelectPanel(messageSource, messageSource.getMessage("startMonitorRecording.chooseSignalFileLabel"));
		}
		return selectSignalRecordingFilePanel;
	}

	public FileSelectPanel getSelectTagsRecordingFilePanel() {
		if (selectTagsRecordingFilePanel == null) {
			selectTagsRecordingFilePanel = new FileSelectPanel(messageSource, messageSource.getMessage("startMonitorRecording.chooseTagFileLabel"));
		}
		return selectTagsRecordingFilePanel;
	}

	public DisableTagRecordingPanel getDisableTagRecordingPanel() {
		if (disableTagRecordingPanel == null) {
			disableTagRecordingPanel = new DisableTagRecordingPanel();
		}
		return disableTagRecordingPanel;
	}

	public void fillModelFromDialog(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = ((OpenMonitorDescriptor) model).getMonitorRecordingDescriptor();
		monitorRecordingDescriptor.setSignalRecordingFilePath(getSelectSignalRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingFilePath(getSelectTagsRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingDisabled(getDisableTagRecordingPanel().isTagRecordingDisabled());
	}

	public void fillDialogFromModel(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = ((OpenMonitorDescriptor) model).getMonitorRecordingDescriptor();
		getSelectSignalRecordingFilePanel().setFileName(monitorRecordingDescriptor.getSignalRecordingFilePath());
		getSelectTagsRecordingFilePanel().setFileName(monitorRecordingDescriptor.getTagsRecordingFilePath());
		getDisableTagRecordingPanel().setTagRecordingDisabled(monitorRecordingDescriptor.isTagsRecordingDisabled());
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		for (Component component : this.getComponents()) {
			component.setEnabled(enabled);
		}
	}

	public void validatePanel(Object model, Errors errors) {
		if (getSelectSignalRecordingFilePanel().getFileName().isEmpty()) {
			errors.reject("error.startMonitorRecording.incorrectSignalFile");
		}
		if (getDisableTagRecordingPanel().isTagRecordingEnabled() && getSelectTagsRecordingFilePanel().getFileName().isEmpty()) {
			errors.reject("error.startMonitorRecording.incorrectTagFile");
		}
	}

	private class DisableTagRecordingPanel extends JPanel {

		private JCheckBox disableTagRecordingCheckBox = null;

		public DisableTagRecordingPanel() {

			disableTagRecordingCheckBox = new JCheckBox();
			disableTagRecordingCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						getSelectTagsRecordingFilePanel().setEnabled(false);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						getSelectTagsRecordingFilePanel().setEnabled(true);
					}
				}
			});
			add(disableTagRecordingCheckBox);
			add(new JLabel(messageSource.getMessage("startMonitorRecording.doNotRecordTagsLabel")));
		}

		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);

			for (Component component : this.getComponents()) {
				component.setEnabled(enabled);
			}
		}

		public boolean isTagRecordingDisabled() {
			return disableTagRecordingCheckBox.isSelected();
		}

		public boolean isTagRecordingEnabled() {
			return !isTagRecordingDisabled();
		}

		public void setTagRecordingDisabled(boolean disable) {
			disableTagRecordingCheckBox.setSelected(disable);
		}
	}
}
