/* RequiredSignalParametersPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.element;

import java.awt.Dimension;

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
import org.springframework.context.support.MessageSourceAccessor;
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
public class RequiredSignalParametersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RequiredSignalParametersPanel.class);

	/**
	 * the source of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * the text field with the sampling frequency (Hz)
	 */
	private JTextField samplingFrequencyField;
	/**
	 * the text field with the number of channels
	 */
	private JTextField channelCountField;
	/**
	 * the text field with the value of calibration
	 */
	private JTextField calibrationField;

	/**
	 * Constructor. Sets the source of messages and initializes this panel.
	 * @param messageSource the source of messages
	 */
	public RequiredSignalParametersPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
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
		        new TitledBorder(messageSource.getMessage("signalParameters.requiredSignalParameters")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel samplingFrequencyLabel = new JLabel(messageSource.getMessage("signalParameters.samplingFrequency"));
		JLabel channelCountLabel = new JLabel(messageSource.getMessage("signalParameters.channelCount"));
		JLabel calibrationLabel = new JLabel(messageSource.getMessage("signalParameters.calibration"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(samplingFrequencyLabel)
		        .addComponent(channelCountLabel)
		        .addComponent(calibrationLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getSamplingFrequencyField())
		        .addComponent(getChannelCountField())
		        .addComponent(getCalibrationField())
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
				.addComponent(calibrationLabel)
				.addComponent(getCalibrationField())
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
	 * Returns the text field with the value of calibration.
	 * If the text field doesn't exist it is created.
	 * @return the text field with the value of calibration
	 */
	public JTextField getCalibrationField() {
		if (calibrationField == null) {
			calibrationField = new JTextField();
			calibrationField.setPreferredSize(new Dimension(200,25));
		}
		return calibrationField;
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

		Float calibration = spd.getCalibration();
		if (calibration != null) {
			getCalibrationField().setText(calibration.toString());
		} else {
			getCalibrationField().setText("");
		}

		if (spd.isSamplingFrequencyEditable()) {
			getSamplingFrequencyField().setEditable(true);
			getSamplingFrequencyField().setToolTipText(null);
		} else {
			getSamplingFrequencyField().setEditable(false);
			getSamplingFrequencyField().setToolTipText(messageSource.getMessage("signalParameters.requiredNotEditable"));
		}

		if (spd.isChannelCountEditable()) {
			getChannelCountField().setEditable(true);
			getChannelCountField().setToolTipText(null);
		} else {
			getChannelCountField().setEditable(false);
			getChannelCountField().setToolTipText(messageSource.getMessage("signalParameters.requiredNotEditable"));
		}

		if (spd.isCalibrationEditable()) {
			getCalibrationField().setEditable(true);
			getCalibrationField().setToolTipText(null);
		} else {
			getCalibrationField().setEditable(false);
			getCalibrationField().setToolTipText(messageSource.getMessage("signalParameters.requiredNotEditable"));
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
				spd.setCalibration(new Float(getCalibrationField().getText()));
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
				float calibration = Float.parseFloat(getCalibrationField().getText());
				if (calibration <= 0) {
					errors.rejectValue("calibration", "error.calibrationNegative");
				}
			} catch (NumberFormatException ex) {
				errors.rejectValue("calibration", "error.invalidNumber");
			}
		}
	}

}
