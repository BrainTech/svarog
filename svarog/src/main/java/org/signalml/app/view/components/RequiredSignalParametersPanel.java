/* RequiredSignalParametersPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.plugin.export.SignalMLException;

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
	 * the text field with the sampling frequency (Hz)
	 */
	private JTextField samplingFrequencyField;
	/**
	 * the text field with the number of channels
	 */
	private JTextField channelCountField;

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
	 * the number of channels,</li></ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 */
	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
			new TitledBorder(_("Required signal parameters")),
			new EmptyBorder(2,2,2,2)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel samplingFrequencyLabel = new JLabel(_("Sampling Frequency (Hz)"));
		JLabel channelCountLabel = new JLabel(_("Number of channels"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(samplingFrequencyLabel)
			.addComponent(channelCountLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getSamplingFrequencyField())
			.addComponent(getChannelCountField())
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
			samplingFrequencyField.setEditable(false);
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
	 * Using the given {@link SignalParameters model} sets:
	 * <ul>
	 * <li>the sampling frequency,</li>
	 * <li>the number of channels,</li>
	 * <li>the value of calibration</li></ul>
	 * in appropriate text field and sets if these text fields should be
	 * editable.
	 * @param spd the model for this panel
	 * @throws SignalMLException never thrown
	 */
	public void fillPanelFromModel(SignalParameters spd) throws SignalMLException {

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

		if (spd.isChannelCountEditable()) {
			getChannelCountField().setEditable(true);
			getChannelCountField().setToolTipText(null);
		} else {
			getChannelCountField().setEditable(false);
			getChannelCountField().setToolTipText(_("This parameter may not be changed for this signal file"));
		}

	}

	/**
	 * Stores the user input in the {@link SignalParameters model},
	 * namely:
	 * <ul>
	 * <li>the sampling frequency,</li>
	 * <li>the number of channels,</li>
	 * <li>the value of calibration.</li></ul>
	 * @param spd the model for this panel
	 * @throws SignalMLException if the value the text fields has an invalid
	 * format
	 */
	public void fillModelFromPanel(SignalParameters spd) throws SignalMLException {
		try {
			if (spd.isChannelCountEditable()) {
				spd.setChannelCount(new Integer(getChannelCountField().getText()));
			}
		} catch (NumberFormatException ex) {
			throw new SignalMLException(ex);
		}
	}

	/**
	 * Validates this panel.
	 * This panel is valid if all numbers in text fields have valid format and
	 * are positive.
	 * @param spd the {@link SignalParameters model} for this panel
	 * @param errors the object in which errors are stored
	 * @throws SignalMLException never thrown
	 */
	public void validatePanel(SignalParameters spd, ValidationErrors errors) throws SignalMLException {
		if (spd.isChannelCountEditable()) {
			try {
				int channelCount = Integer.parseInt(getChannelCountField().getText());
				if (channelCount <= 0) {
					errors.addError(_("Channel count must be positive"));
				}
			} catch (NumberFormatException ex) {
				errors.addError(_("Invalid numeric value"));
			}
		}
	}

}
