/* RequiredSignalParametersPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/**
 * Panel which allows displays the parameters of the signal, such as:
 * <ul>
 * <li>the {@link #getSamplingFrequencyField() sampling frequency},</li>
 * <li>the {@link #getChannelCountField() number of channels},</li>
 * <li>the {@link #getCalibrationField() value of calibration}</li></ul>
 * and if these fields should be editable, to these values them.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RequiredSignalParametersPanel extends JPanel implements FocusListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RequiredSignalParametersPanel.class);

	/**
	 * the text field with the sampling frequency (Hz)
	 */
	private JTextField samplingFrequencyField;
	/**
	 * the text field with the number of channels
	 */
	private JTextField channelCountField;

	/**
	 * A text field allwing to change the calibration gain.
	 */
	private JTextField calibrationGainField;

	/**
	 * A text field allwing to change the calibration offset.
	 */
	private JTextField calibrationOffsetField;

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

	/**
	 * Constructor. Initializes the panel.
	 */
	public RequiredSignalParametersPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with GroupLayout and two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for spinners. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 3 sub-groups - one for every row:
	 * <ul>
	 * <li>label and {@link #getSamplingFrequencyField() text field} which
	 * contains sampling frequency (Hz),</li>
	 * <li>label and {@link #getChannelCountField() text field} which contains
	 * the number of channels,</li>
	 * <li>label and {@link #getCalibrationField() text field} which contains
	 * the value of calibration.</li></ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 */
	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(_("Required signal parameters")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel samplingFrequencyLabel = new JLabel(_("Sampling Frequency (Hz)"));
		JLabel channelCountLabel = new JLabel(_("Number of channels"));
		JLabel calibrationGainLabel = new JLabel(_("Calibration gain"));
		JLabel calibrationOffsetLabel = new JLabel(_("Calibration offset"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(samplingFrequencyLabel)
		        .addComponent(channelCountLabel)
		        .addComponent(calibrationGainLabel)
			.addComponent(calibrationOffsetLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getSamplingFrequencyField())
		        .addComponent(getChannelCountField())
		        .addComponent(getCalibrationGainField())
			.addComponent(getCalibrationOffsetField())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(samplingFrequencyLabel)
				.addComponent(getSamplingFrequencyField())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(channelCountLabel)
				.addComponent(getChannelCountField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(calibrationGainLabel)
				.addComponent(getCalibrationGainField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(calibrationOffsetLabel)
				.addComponent(getCalibrationOffsetField())
			);

		layout.setVerticalGroup(vGroup);

	}

	/**
	 * Returns the text field with the sampling frequency (in Hz).
	 * If the text field doesn't exist it is created.
	 * @return the text field with the sampling frequency
	 */
	public JTextField getSamplingFrequencyField() {
		if (samplingFrequencyField == null) {
			samplingFrequencyField = new JTextField();
			samplingFrequencyField.setPreferredSize(new Dimension(200,25));
		}
		return samplingFrequencyField;
	}

	/**
	 * Returns the text field with the number of channels in the signal.
	 * If the text field doesn't exist it is created.
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
			calibrationOffsetField.setPreferredSize(new Dimension(200,25));
			calibrationOffsetField.addFocusListener(this);
		}
		return calibrationOffsetField;
	}

	/**
	 * Using the given {@link SignalParameterDescriptor model} sets:
	 * <ul>
	 * <li>the sampling frequency,</li>
	 * <li>the number of channels,</li>
	 * <li>the value of calibration</li></ul>
	 * in appropriate text field and sets if these text fields should be
	 * editable.
	 * @param spd the model for this panel
	 * @throws SignalMLException never thrown
	 */
	public void fillPanelFromModel(SignalParameterDescriptor spd) throws SignalMLException {

		Float samplingFrequency = spd.getSamplingFrequency();
		if (samplingFrequency != null) {
			getSamplingFrequencyField().setText(samplingFrequency.toString());
		} else {
			getSamplingFrequencyField().setText("");
		}

		Integer channelCount = spd.getChannelCount();
		if (channelCount != null) {
			getChannelCountField().setText(channelCount.toString());
		} else {
			getChannelCountField().setText("");
		}

		Float calibration = spd.getCalibrationGain();
		if (calibration != null) {
			getCalibrationGainField().setText(calibration.toString());
		} else {
			getCalibrationGainField().setText("");
		}

		Float calibrationOffset = spd.getCalibrationOffset();
		if (calibrationOffset != null) {
			getCalibrationOffsetField().setText(calibrationOffset.toString());
		} else {
			getCalibrationOffsetField().setText("");
		}

		if (spd.isSamplingFrequencyEditable()) {
			getSamplingFrequencyField().setEditable(true);
			getSamplingFrequencyField().setToolTipText(null);
		} else {
			getSamplingFrequencyField().setEditable(false);
			getSamplingFrequencyField().setToolTipText(_("This parameter may not be changed for this signal file"));
		}

		if (spd.isChannelCountEditable()) {
			getChannelCountField().setEditable(true);
			getChannelCountField().setToolTipText(null);
		} else {
			getChannelCountField().setEditable(false);
			getChannelCountField().setToolTipText(_("This parameter may not be changed for this signal file"));
		}

		if (spd.isCalibrationEditable()) {
			getCalibrationGainField().setEditable(true);
			getCalibrationGainField().setToolTipText(null);
			getCalibrationOffsetField().setEditable(true);
			getCalibrationOffsetField().setToolTipText(null);
		} else {
			getCalibrationGainField().setEditable(false);
			getCalibrationGainField().setToolTipText(_("This parameter may not be changed for this signal file"));
			getCalibrationOffsetField().setToolTipText(_("This parameter may not be changed for this signal file"));
			getCalibrationOffsetField().setEditable(false);
		}

	}

	/**
	 * Stores the user input in the {@link SignalParameterDescriptor model},
	 * namely:
	 * <ul>
	 * <li>the sampling frequency,</li>
	 * <li>the number of channels,</li>
	 * <li>the value of calibration.</li></ul>
	 * @param spd the model for this panel
	 * @throws SignalMLException if the value the text fields has an invalid
	 * format
	 */
	public void fillModelFromPanel(SignalParameterDescriptor spd) throws SignalMLException {
		try {
			if (spd.isSamplingFrequencyEditable()) {
				spd.setSamplingFrequency(new Float(getSamplingFrequencyField().getText()));
			}
			if (spd.isChannelCountEditable()) {
				spd.setChannelCount(new Integer(getChannelCountField().getText()));
			}
			if (spd.isCalibrationEditable()) {

				if (wasCalibrationGainFocused) {
					spd.setCalibrationGain(Float.parseFloat(getCalibrationGainField().getText()));
					wasCalibrationGainFocused = false;
				}
				if (wasCalibrationOffsetFocused) {
					spd.setCalibrationOffset(Float.parseFloat(getCalibrationOffsetField().getText()));
					wasCalibrationOffsetFocused = false;
				}

			}

		} catch (NumberFormatException ex) {
			throw new SignalMLException(ex);
		}
	}

	/**
	 * Validates this panel.
	 * This panel is valid if all numbers in text fields have valid format and
	 * are positive.
	 * @param spd the {@link SignalParameterDescriptor model} for this panel
	 * @param errors the object in which errors are stored
	 * @throws SignalMLException never thrown
	 */
	public void validatePanel(SignalParameterDescriptor spd, Errors errors) throws SignalMLException {
		if (spd.isSamplingFrequencyEditable()) {
			try {
				float samplingFrequency = Float.parseFloat(getSamplingFrequencyField().getText());
				if (samplingFrequency <= 0) {
					errors.rejectValue("samplingFrequency", "error.samplingFrequencyNegative");
				}
			} catch (NumberFormatException ex) {
				errors.rejectValue("samplingFrequency", "error.invalidNumber");
			}
		}
		if (spd.isChannelCountEditable()) {
			try {
				int channelCount = Integer.parseInt(getChannelCountField().getText());
				if (channelCount <= 0) {
					errors.rejectValue("channelCount", "error.channelCountNegative");
				}
			} catch (NumberFormatException ex) {
				errors.rejectValue("channelCount", "error.invalidNumber");
			}
		}
		if (spd.isCalibrationEditable()) {
			try {
				float calibration = Float.parseFloat(getCalibrationGainField().getText());
				if (calibration <= 0) {
					errors.rejectValue("calibration", "error.calibrationNegative");
				}
			} catch (NumberFormatException ex) {
				errors.rejectValue("calibration", "error.invalidNumber");
			}
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
