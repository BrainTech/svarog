/* ChooseFilesForMonitorRecordingPanel.java created 2010-11-03
 *
 */
package org.signalml.app.view.monitor;

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
import org.signalml.app.view.element.FileSelectPanel;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class ChooseFilesForMonitorRecordingPanel extends JPanel {

	private final MessageSourceAccessor messageSource;
	private FileSelectPanel selectSignalRecordingFilePanel;
	private FileSelectPanel selectTagsRecordingFilePanel;
	private JPanel disableTagRecordingPanel;
	private JCheckBox disableTagRecordingCheckBox;

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

	public JPanel getDisableTagRecordingPanel() {
		if (disableTagRecordingPanel == null) {
			disableTagRecordingPanel = new JPanel();
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
			disableTagRecordingPanel.add(disableTagRecordingCheckBox);
			disableTagRecordingPanel.add(new JLabel(messageSource.getMessage("startMonitorRecording.doNotRecordTagsLabel")));
		}
		return disableTagRecordingPanel;
	}

	public void fillModelFromDialog(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = (MonitorRecordingDescriptor) model;
		monitorRecordingDescriptor.setSignalRecordingFilePath(getSelectSignalRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingFilePath(getSelectTagsRecordingFilePanel().getFileName());
		monitorRecordingDescriptor.setTagsRecordingDisabled(disableTagRecordingCheckBox.isSelected());
	}

	public void fillDialogFromModel(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = (MonitorRecordingDescriptor) model;
		getSelectSignalRecordingFilePanel().setFileName(monitorRecordingDescriptor.getSignalRecordingFilePath());
		getSelectTagsRecordingFilePanel().setFileName(monitorRecordingDescriptor.getTagsRecordingFilePath());
		disableTagRecordingCheckBox.setSelected(monitorRecordingDescriptor.isTagsRecordingDisabled());
	}
}
