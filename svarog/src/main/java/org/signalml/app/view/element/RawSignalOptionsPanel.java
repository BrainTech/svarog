/* RawSignalOptionsPanel.java created 2008-01-28
 *
 */

package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
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

import org.springframework.validation.Errors;


/**
 * The panel which allows to select the parameters of the raw signal:
 * <ul>
 * <li>the sampling frequency,</li>
 * <li>the number of channels,</li>
 * <li>the {@link RawSignalSampleType type} of samples,</li>
 * <li>the {@link RawSignalByteOrder order} of bytes,</li>
 * <li>the value of calibration.</li>
 * </ul>
 * Contains also the button which reads these parameters from an XML file.
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalOptionsPanel extends JPanel implements FocusListener {

	private static final long serialVersionUID = 1L;

	/**
	 * the text field with the number of samples per second of the signal
	 */
	private JTextField samplingFrequencyField;
	/**
	 * the text field with the number of channels
	 */
	private JTextField channelCountField;

	/**
	 * Text field for setting the value of calibration gain.
	 */
	private JTextField calibrationGainField;

	/**
	 * Text field for setting the value of calibration offset.
	 */
	private JTextField calibrationOffsetField;

	/**
	 * the combo-box which allows to select the {@link RawSignalSampleType
	 * type} of signal samples (SHORT, INT, FLOAT, DOUBLE)
	 */
	private ResolvableComboBox sampleTypeComboBox;
	/**
	 * the combo-box which allows to select the {@link RawSignalByteOrder
	 * order} of bytes (little or big endian)
	 */
	private ResolvableComboBox byteOrderComboBox;

	/**
	 * the button which reads the values of the fields in this panel from an
	 * XML file
	 */
	private JButton readXMLManifestButton;

	/**
	 * the panel with the parameters of the signal, see
	 * {@link #getSettingsPanel()}
	 */
	private JPanel settingsPanel;
	/**
	 * the panel with {@link #readXMLManifestButton}
	 */
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

	public RawSignalOptionsPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with a {@link BorderLayout} and adds two
	 * sub-panels to it:
	 * <ul><li>the {@link #getSettingsPanel() panel} with the parameters of the
	 * signal,</li>
	 * <li>the panel with {@link #readXMLManifestButton}.</li>
	 * </ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(_("Raw signal options")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		add(getSettingsPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);

	}

	/**
	 * Returns the text field with the number of samples per second of the
	 * signal.
	 * If it doesn't exist it is created.
	 * @return the text field with the number of samples per second of the
	 * signal
	 */
	public JTextField getSamplingFrequencyField() {
		if (samplingFrequencyField == null) {
			samplingFrequencyField = new JTextField();
			samplingFrequencyField.setPreferredSize(new Dimension(200,25));
		}
		return samplingFrequencyField;
	}

	/**
	 * Returns the text field with the number of channels.
	 * If it doesn't exist it is created.
	 * @return the text field with the number of channels
	 */
	public JTextField getChannelCountField() {
		if (channelCountField == null) {
			channelCountField = new JTextField();
			channelCountField.setPreferredSize(new Dimension(200,25));
		}
		return channelCountField;
	}

	/**
	 * Returns the text field allowing to change the calibration gain
	 * of the signal.
	 * @return a text filed allowing to set the calibration gain
	 */
	public JTextField getCalibrationGainField() {
		if (calibrationGainField == null) {
			calibrationGainField = new JTextField();
			calibrationGainField.setPreferredSize(new Dimension(200,25));
			calibrationGainField.addFocusListener(this);
		}
		return calibrationGainField;
	}

	/**
	 * Returns the text field allowing to change the calibration offset
	 * of the signal.
	 * @return a text filed allowing to set the calibration offset
	 */
	public JTextField getCalibrationOffsetField() {
		if (calibrationOffsetField == null) {
			calibrationOffsetField = new JTextField();
			calibrationOffsetField.setPreferredSize(new Dimension(200, 25));
			calibrationOffsetField.addFocusListener(this);
		}
		return calibrationOffsetField;
	}

	/**
	 * Returns the combo-box which allows to select the
	 * {@link RawSignalSampleType type} of signal samples (SHORT, INT, FLOAT,
	 * DOUBLE).
	 * If it doesn't exist it is created and possible
	 * {@link RawSignalSampleType#values() values} are added to it.
	 * @return the combo-box which allows to select the type of signal samples
	 */
	public ResolvableComboBox getSampleTypeComboBox() {
		if (sampleTypeComboBox == null) {
			sampleTypeComboBox = new ResolvableComboBox();
			sampleTypeComboBox.setModel(new DefaultComboBoxModel(RawSignalSampleType.values()));
			sampleTypeComboBox.setPreferredSize(new Dimension(80,25));
		}
		return sampleTypeComboBox;
	}

	/**
	 * Returns the combo-box which allows to select the
	 * {@link RawSignalByteOrder order} of bytes (little or big endian).
	 * If it doesn't exist it is created and possible
	 * {@link RawSignalByteOrder#values() values} are added to it.
	 * @return the combo-box which allows to select the order of bytes
	 */
	public ResolvableComboBox getByteOrderComboBox() {
		if (byteOrderComboBox == null) {
			byteOrderComboBox = new ResolvableComboBox();
			byteOrderComboBox.setModel(new DefaultComboBoxModel(RawSignalByteOrder.values()));
			byteOrderComboBox.setPreferredSize(new Dimension(80,25));
		}
		return byteOrderComboBox;
	}

	/**
	 * Returns the button which reads the values of the fields in this panel
	 * from an XML file.
	 * If it doesn't exist it is created.
	 * @return the button which reads the values of the fields in this panel
	 * from an XML file
	 */
	public JButton getReadXMLManifestButton() {
		if (readXMLManifestButton == null) {
			readXMLManifestButton = new JButton(); // action is set outside
		}
		return readXMLManifestButton;
	}

	/**
	 * Returns the panel with the {@link #getReadXMLManifestButton() button}
	 * which reads the values of the fields in this panel from an XML file.
	 * If the panel doesn't exist it is created.
	 * @return the panel with the button which reads the values of the fields
	 * in this panel from an XML file
	 */
	public JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING,3,3));

			buttonPanel.add(getReadXMLManifestButton());
		}
		return buttonPanel;
	}

	/**
	 * Returns the panel with the parameters of the signal.
	 * The panel contains two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for combo-boxes and text fields.
	 * This group positions the elements in two columns.</li>
	 * <li>vertical group which has 5 sub-groups - one for every row:
	 * <ul>
	 * <li>the label and the text field with the sampling frequency,</li>
	 * <li>the label and the text field with the number of channels,</li>
	 * <li>the label and the combo-box which allows to select the
	 * {@link RawSignalSampleType type} of samples,</li>
	 * <li>the label and the combo-box which allows to select the
	 * {@link RawSignalByteOrder order} of bytes,</li>
	 * <li>the label and the text field with the value of calibration,</li>
	 * </ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 * If the panel doesn't exist it is created.
	 * @return the panel with the parameters of the signal
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null) {

			settingsPanel = new JPanel(null);

			GroupLayout layout = new GroupLayout(settingsPanel);
			settingsPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel samplingFrequencyLabel = new JLabel(_("Sampling Frequency (Hz)"));
			JLabel channelCountLabel = new JLabel(_("Number of channels"));
			JLabel sampleTypeLabel = new JLabel(_("Sample type"));
			JLabel byteOrderLabel = new JLabel(_("Byte order"));
			JLabel calibrationGainLabel = new JLabel(_("Calibration gain"));
			JLabel calibrationOffsetLabel = new JLabel(_("Calibration offset"));

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

	/**
	 * Fills the fields of this panel using the given
	 * {@link RawSignalDescriptor descriptor}:
	 * <ul>
	 * <li>the {@link RawSignalDescriptor#getSamplingFrequency() sampling
	 * frequency},</li>
	 * <li>the {@link RawSignalDescriptor#getChannelCount() number} of
	 * channels,</li>
	 * <li>the selected {@link RawSignalDescriptor#getSampleType() sample
	 * type},</li>
	 * <li>the selected {@link RawSignalDescriptor#getByteOrder() byte order},
	 * </li>
	 * <li>the {@link RawSignalDescriptor#getCalibration() calibration}.</li>
	 * </ul>
	 * @param descriptor the descriptor
	 */
	public void fillPanelFromModel(RawSignalDescriptor descriptor) {

		getSamplingFrequencyField().setText(Float.toString(descriptor.getSamplingFrequency()));
		getChannelCountField().setText(Integer.toString(descriptor.getChannelCount()));
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getCalibrationGainField().setText(Float.toString(descriptor.getCalibrationGain()[0])); //TODO: these should be changed as soon as possible - a panel to change individual channel's gain and offset should be provided.
		getCalibrationOffsetField().setText(Float.toString(descriptor.getCalibrationOffset()[0]));

	}

	/**
	 * Fills the given {@link RawSignalDescriptor descriptor} with the user
	 * input from this panel:
	 * <ul>
	 * <li>the {@link RawSignalDescriptor#setSamplingFrequency(float) sampling
	 * frequency},</li>
	 * <li>the {@link RawSignalDescriptor#setChannelCount(int) number} of
	 * channels,</li>
	 * <li>the selected {@link RawSignalDescriptor#setSampleType(
	 * RawSignalSampleType) sample type},</li>
	 * <li>the selected {@link RawSignalDescriptor#setByteOrder(
	 * RawSignalByteOrder) byte order},</li>
	 * <li>the {@link RawSignalDescriptor#setCalibration(float) calibration}.
	 * </li>
	 * </ul>
	 * @param descriptor the descriptor to fill
	 */
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

	/**
	 * Validates this dialog.
	 * Dialog is valid if all numbers in text field have valid format and
	 * are positive.
	 * @param errors the object in which the errors are stored
	 * @throws SignalMLException never thrown
	 */
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
