/* RawSignalOptionsPanel.java created 2008-01-28
 *
 */

package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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
public class RawSignalOptionsPanel extends JPanel implements FocusListener {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JTextField samplingFrequencyField;
	private JTextField channelCountField;

	/**
	 * Text field for setting the value of calibration gain.
	 */
	private JTextField calibrationGainField;

	/**
	 * Text field for setting the value of calibration offset.
	 */
	private JTextField calibrationOffsetField;

	private ResolvableComboBox sampleTypeComboBox;
	private ResolvableComboBox byteOrderComboBox;

	private JButton readXMLManifestButton;

	private JPanel settingsPanel;
	private JPanel buttonPanel;

	/**
	 * True if the calibration gain field was focused, false otherwise.
	 * It is assumed that if this field was focused, then it probably
	 * was also modified, so the descriptor for the signal is also modified.
	 *
	 * TODO: This is a temporary solution - RawSignalSampleSource supports
	 * individual calibrations for each channel, so there should be a
	 * possiblity to edit calibration gain and offeset for each channel
	 * separately. In that case these boolean variables would not be needed.
	 */
	private boolean wasCalibrationGainFocused = false;

	/**
	 * True if the calibration offset field was focused, false otherwise.
	 */
	private boolean wasCalibrationOffsetFocused = false;

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

	public JTextField getCalibrationGainField() {
		if (calibrationGainField == null) {
			calibrationGainField = new JTextField();
			calibrationGainField.setPreferredSize(new Dimension(200,25));
			calibrationGainField.addFocusListener(this);
		}
		return calibrationGainField;
	}

	public JTextField getCalibrationOffsetField() {
		if (calibrationOffsetField == null) {
			calibrationOffsetField = new JTextField();
			calibrationOffsetField.setPreferredSize(new Dimension(200, 25));
			calibrationOffsetField.addFocusListener(this);
		}
		return calibrationOffsetField;
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
			JLabel calibrationGainLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.calibrationGain"));
			JLabel calibrationOffsetLabel = new JLabel(messageSource.getMessage("openSignal.options.raw.calibrationOffset"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(layout.createParallelGroup()
			                .addComponent(samplingFrequencyLabel)
			                .addComponent(channelCountLabel)
			                .addComponent(sampleTypeLabel)
			                .addComponent(byteOrderLabel)
			                .addComponent(calibrationGainLabel)
					.addComponent(calibrationOffsetLabel)
			               );

			hGroup.addGroup(layout.createParallelGroup()
			                .addComponent(getSamplingFrequencyField())
			                .addComponent(getChannelCountField())
			                .addComponent(getSampleTypeComboBox())
			                .addComponent(getByteOrderComboBox())
			                .addComponent(getCalibrationGainField())
					.addComponent(getCalibrationOffsetField())
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
					.addComponent(calibrationGainLabel)
					.addComponent(getCalibrationGainField())
				);

			vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(calibrationOffsetLabel)
					.addComponent(getCalibrationOffsetField())
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
		getCalibrationGainField().setText(Float.toString(descriptor.getCalibrationGain()[0])); //TODO: these should be changed as soon as possible - a panel to change individual channel's gain and offset should be provided.
		getCalibrationOffsetField().setText(Float.toString(descriptor.getCalibrationOffset()[0]));

	}

	public void fillModelFromPanel(RawSignalDescriptor descriptor) {

		descriptor.setSamplingFrequency(Float.parseFloat(getSamplingFrequencyField().getText()));
		descriptor.setChannelCount(Integer.parseInt(getChannelCountField().getText()));
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());

		if (wasCalibrationGainFocused) {
			descriptor.setCalibrationGain(Float.parseFloat(getCalibrationGainField().getText()));
			wasCalibrationGainFocused = false;
		}
		if (wasCalibrationOffsetFocused) {
			descriptor.setCalibrationOffset(Float.parseFloat(getCalibrationOffsetField().getText()));
			wasCalibrationOffsetFocused = false;
		}

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
			float calibration = Float.parseFloat(getCalibrationGainField().getText());
			if (calibration <= 0) {
				errors.rejectValue("calibration", "error.calibrationNegative");
			}
		} catch (NumberFormatException ex) {
			errors.rejectValue("calibration", "error.invalidNumber");
		}

	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == getCalibrationGainField()) {
			wasCalibrationGainFocused = true;
		}
		else if (e.getSource() == getCalibrationOffsetField()) {
			wasCalibrationOffsetFocused = true;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

}
