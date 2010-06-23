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
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** RequiredSignalParametersPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RequiredSignalParametersPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RequiredSignalParametersPanel.class);
	
	private MessageSourceAccessor messageSource;
	
	private JTextField samplingFrequencyField;
	private JTextField channelCountField;
	private JTextField calibrationField;
		
	/**
	 * This is the default constructor
	 */
	public RequiredSignalParametersPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
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

	public JTextField getSamplingFrequencyField() {
		if( samplingFrequencyField == null ) {
			samplingFrequencyField = new JTextField();
			samplingFrequencyField.setPreferredSize(new Dimension(200,25));
		}
		return samplingFrequencyField;
	}

	public JTextField getChannelCountField() {
		if( channelCountField == null ) {
			channelCountField = new JTextField();			
			channelCountField.setPreferredSize(new Dimension(200,25));
		}
		return channelCountField;
	}

	public JTextField getCalibrationField() {
		if( calibrationField == null ) {
			calibrationField = new JTextField();			
			calibrationField.setPreferredSize(new Dimension(200,25));
		}
		return calibrationField;
	}

	public void fillPanelFromModel( SignalParameterDescriptor spd ) throws SignalMLException {
		
		Float samplingFrequency = spd.getSamplingFrequency();
		if( samplingFrequency != null ) {
			getSamplingFrequencyField().setText( samplingFrequency.toString() );
		} else {
			getSamplingFrequencyField().setText( "" );
		}
		
		Integer channelCount = spd.getChannelCount();
		if( channelCount != null ) {
			getChannelCountField().setText( channelCount.toString() );
		} else {
			getChannelCountField().setText( "" );
		}
		
		Float calibration = spd.getCalibration();
		if( calibration != null ) {
			getCalibrationField().setText( calibration.toString() );
		} else {
			getCalibrationField().setText( "" );
		}

		if( spd.isSamplingFrequencyEditable() ) {
			getSamplingFrequencyField().setEditable(true);
			getSamplingFrequencyField().setToolTipText(null);
		} else {
			getSamplingFrequencyField().setEditable(false);
			getSamplingFrequencyField().setToolTipText(messageSource.getMessage("signalParameters.requiredNotEditable"));
		}
		
		if( spd.isChannelCountEditable() ) {
			getChannelCountField().setEditable(true);
			getChannelCountField().setToolTipText(null);
		} else {
			getChannelCountField().setEditable(false);
			getChannelCountField().setToolTipText(messageSource.getMessage("signalParameters.requiredNotEditable"));
		}
		
		if( spd.isCalibrationEditable() ) {
			getCalibrationField().setEditable(true);
			getCalibrationField().setToolTipText(null);
		} else {
			getCalibrationField().setEditable(false);
			getCalibrationField().setToolTipText(messageSource.getMessage("signalParameters.requiredNotEditable"));
		}
		
	}
	
	public void fillModelFromPanel( SignalParameterDescriptor spd ) throws SignalMLException {
		try {
			if( spd.isSamplingFrequencyEditable() ) {
				spd.setSamplingFrequency( new Float(getSamplingFrequencyField().getText()) );
			}
			if( spd.isChannelCountEditable() ) {
				spd.setChannelCount( new Integer(getChannelCountField().getText()) );
			}
			if( spd.isCalibrationEditable() ) { 
				spd.setCalibration( new Float(getCalibrationField().getText()) );
			}
		} catch( NumberFormatException ex ) {
			throw new SignalMLException(ex);
		}
	}
	
	public void validatePanel( SignalParameterDescriptor spd, Errors errors ) throws SignalMLException {
		if( spd.isSamplingFrequencyEditable() ) {
			try {
				float samplingFrequency = Float.parseFloat(getSamplingFrequencyField().getText());
				if( samplingFrequency <= 0 ) {
					errors.rejectValue("samplingFrequency", "error.samplingFrequencyNegative");
				}
			} catch( NumberFormatException ex ) {
				errors.rejectValue("samplingFrequency", "error.invalidNumber");
			}
		}
		if( spd.isChannelCountEditable() ) {
			try {
				int channelCount = Integer.parseInt(getChannelCountField().getText());
				if( channelCount <= 0 ) {
					errors.rejectValue("channelCount", "error.channelCountNegative");
				}
			} catch( NumberFormatException ex ) {
				errors.rejectValue("channelCount", "error.invalidNumber");
			}
		}
		if( spd.isCalibrationEditable() ) {
			try {
				float calibration = Float.parseFloat(getCalibrationField().getText());
				if( calibration <= 0 ) {
					errors.rejectValue("calibration", "error.calibrationNegative");
				}
			} catch( NumberFormatException ex ) {
				errors.rejectValue("calibration", "error.invalidNumber");
			}
		}
	}
	
}
