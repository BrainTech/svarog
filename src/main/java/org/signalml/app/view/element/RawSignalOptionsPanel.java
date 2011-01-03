/* RawSignalOptionsPanel.java created 2008-01-28
 *
 */

package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** RawSignalOptionsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JTextField samplingFrequencyField;
	private JTextField channelCountField;
	private JTextField calibrationField;

	private ResolvableComboBox sampleTypeComboBox;
	private ResolvableComboBox byteOrderComboBox;

	private JButton readXMLManifestButton;

	private JPanel settingsPanel;
	private JPanel buttonPanel;

	public RawSignalOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("openSignal.options.raw.title")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		add(getSettingsPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);

	}

	public JTextField getSamplingFrequencyField() {
		if (samplingFrequencyField == null) {
			samplingFrequencyField = new JTextField();
			samplingFrequencyField.setPreferredSize(new Dimension(200,25));
		}
		return samplingFrequencyField;
	}

	public JTextField getChannelCountField() {
		if (channelCountField == null) {
			channelCountField = new JTextField();
			channelCountField.setPreferredSize(new Dimension(200,25));
		}
		return channelCountField;
	}

	public JTextField getCalibrationField() {
		if (calibrationField == null) {
			calibrationField = new JTextField();
			calibrationField.setPreferredSize(new Dimension(200,25));
			calibrationField.setEditable(false);
		}
		return calibrationField;
	}

	public ResolvableComboBox getSampleTypeComboBox() {
		if (sampleTypeComboBox == null) {
			sampleTypeComboBox = new ResolvableComboBox(messageSource);
			sampleTypeComboBox.setModel(new DefaultComboBoxModel(RawSignalSampleType.values()));
			sampleTypeComboBox.setPreferredSize(new Dimension(80,25));
		}
		return sampleTypeComboBox;
	}

	public ResolvableComboBox getByteOrderComboBox() {
		if (byteOrderComboBox == null) {
			byteOrderComboBox = new ResolvableComboBox(messageSource);
			byteOrderComboBox.setModel(new DefaultComboBoxModel(RawSignalByteOrder.values()));
			byteOrderComboBox.setPreferredSize(new Dimension(80,25));
		}
		return byteOrderComboBox;
	}

	public JButton getReadXMLManifestButton() {
		if (readXMLManifestButton == null) {
			readXMLManifestButton = new JButton(); // action is set outside
		}
		return readXMLManifestButton;
	}

	public JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING,3,3));

			buttonPanel.add(getReadXMLManifestButton());
		}
		return buttonPanel;
	}

	public JPanel getSettingsPanel() {
		if (settingsPanel == null) {

			settingsPanel = new JPanel(null);

			GroupLayout layout = new GroupLayout(settingsPanel);
			settingsPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel samplingFrequencyLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.samplingFrequency"));
			JLabel channelCountLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.channelCount"));
			JLabel sampleTypeLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.sampleType"));
			JLabel byteOrderLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.byteOrder"));
			JLabel calibrationLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.calibration"));


			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(layout.createParallelGroup()
			                .addComponent(samplingFrequencyLabel)
			                .addComponent(channelCountLabel)
			                .addComponent(sampleTypeLabel)
			                .addComponent(byteOrderLabel)
			                .addComponent(calibrationLabel)
			               );

			hGroup.addGroup(layout.createParallelGroup()
			                .addComponent(getSamplingFrequencyField())
			                .addComponent(getChannelCountField())
			                .addComponent(getSampleTypeComboBox())
			                .addComponent(getByteOrderComboBox())
			                .addComponent(getCalibrationField())
			               );

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(samplingFrequencyLabel)
					.addComponent(getSamplingFrequencyField())
				);
			
			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(channelCountLabel)
					.addComponent(getChannelCountField())
				);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(sampleTypeLabel)
					.addComponent(getSampleTypeComboBox())
				);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(byteOrderLabel)
					.addComponent(getByteOrderComboBox())
				);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(calibrationLabel)
					.addComponent(getCalibrationField())
				);

			layout.setVerticalGroup(vGroup);

		}

		return settingsPanel;

	}

	public void fillPanelFromModel(RawSignalDescriptor descriptor) {

		getSamplingFrequencyField().setText(Float.toString(descriptor.getSamplingFrequency()));
		getChannelCountField().setText(Integer.toString(descriptor.getChannelCount()));
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getCalibrationField().setText(Float.toString(descriptor.getCalibrationGain()[0]));

	}

	public void fillModelFromPanel(RawSignalDescriptor descriptor) {

		descriptor.setSamplingFrequency(Float.parseFloat(getSamplingFrequencyField().getText()));
		descriptor.setChannelCount(Integer.parseInt(getChannelCountField().getText()));
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
		//descriptor.setCalibrationGain(Float.parseFloat(getCalibrationField().getText()));

	}

	public void validatePanel(Errors errors) throws SignalMLException {

		try {
			float samplingFrequency = Float.parseFloat(getSamplingFrequencyField().getText());
			if (samplingFrequency <= 0) {
				errors.rejectValue("samplingFrequency", "error.samplingFrequencyNegative");
			}
		} catch (NumberFormatException ex) {
			errors.rejectValue("samplingFrequency", "error.invalidNumber");
		}

		try {
			int channelCount = Integer.parseInt(getChannelCountField().getText());
			if (channelCount <= 0) {
				errors.rejectValue("channelCount", "error.channelCountNegative");
			}
		} catch (NumberFormatException ex) {
			errors.rejectValue("channelCount", "error.invalidNumber");
		}

		try {
			float calibration = Float.parseFloat(getCalibrationField().getText());
			if (calibration <= 0) {
				errors.rejectValue("calibration", "error.calibrationNegative");
			}
		} catch (NumberFormatException ex) {
			errors.rejectValue("calibration", "error.invalidNumber");
		}

	}

}
