/* MonitorRecordingPanel.java created 2010-11-09
 *
 */
package org.signalml.app.view.element;

import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.monitor.ChooseFilesForMonitorRecordingPanel;

import org.springframework.context.support.MessageSourceAccessor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.MonitorRecordingDescriptor;
import org.springframework.validation.Errors;

/**
 *
 * @author Piotr Szachewicz
 */
public class MonitorRecordingPanel extends JPanel {

	private final MessageSourceAccessor messageSource;
	private ChooseFilesForMonitorRecordingPanel chooseFilesForMonitorRecordingPanel = null;
	private JPanel enableRecordingPanel = null;
	private JCheckBox enableRecordingCheckbox = null;

	public MonitorRecordingPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(10, 10));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("openMonitor.monitorRecordingPanelTitle")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		add(getEnableRecordingPanel(), BorderLayout.NORTH);
		add(getChooseFilesForMonitorRecordingPanel(), BorderLayout.CENTER);

		getEnableRecordingCheckbox().setSelected(false);
		getChooseFilesForMonitorRecordingPanel().setEnabled(false);

	}

	protected ChooseFilesForMonitorRecordingPanel getChooseFilesForMonitorRecordingPanel() {
		if (chooseFilesForMonitorRecordingPanel == null) {
			chooseFilesForMonitorRecordingPanel = new ChooseFilesForMonitorRecordingPanel(messageSource);
		}
		return chooseFilesForMonitorRecordingPanel;
	}

	protected JPanel getEnableRecordingPanel() {
		if (enableRecordingPanel == null) {
			enableRecordingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			enableRecordingPanel.add(getEnableRecordingCheckbox());
			enableRecordingPanel.add(new JLabel("enable recording"));
		}
		return enableRecordingPanel;
	}

	protected JCheckBox getEnableRecordingCheckbox() {
		if (enableRecordingCheckbox == null) {
			enableRecordingCheckbox = new JCheckBox();
			enableRecordingCheckbox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (enableRecordingCheckbox.isSelected()) {
						getChooseFilesForMonitorRecordingPanel().setEnabled(true);
					} else {
						getChooseFilesForMonitorRecordingPanel().setEnabled(false);
					}
				}
			});
		}
		return enableRecordingCheckbox;
	}

	public boolean isRecordingEnabled() {
		return enableRecordingCheckbox.isSelected();
	}

	public void fillModelFromPanel(OpenMonitorDescriptor openMonitorDescriptor) {
		if (isRecordingEnabled()) {
			openMonitorDescriptor.getMonitorRecordingDescriptor().setRecordingEnabled(true);
			getChooseFilesForMonitorRecordingPanel().fillModelFromDialog(openMonitorDescriptor);
		} else {
			openMonitorDescriptor.getMonitorRecordingDescriptor().setRecordingEnabled(false);
		}
	}

	public void validatePanel(Object model, Errors errors) {
		if (isRecordingEnabled()) {
			getChooseFilesForMonitorRecordingPanel().validatePanel(model, errors);
		}
	}
}
