package org.signalml.app.view.element;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 */
public class MonitorParamsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MonitorParamsPanel.class);
	
	private MessageSourceAccessor messageSource;

    private JTextField samplingField;
    private JTextField channelCountField;
    private JTextField calibrationGainField;
    private JTextField calibrationOffsetField;

    private JTextField pageSizeField;

	/**
	 * This is the default constructor
	 */
	public MonitorParamsPanel( MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
//		this.appConfig = appConfig;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

        JLabel samplingLabel = new JLabel( messageSource.getMessage( "openMonitor.samplingLabel"));
        JLabel channelCountLabel = new JLabel( messageSource.getMessage("openMonitor.channelCountLabel"));
        JLabel calibrationGainLabel = new JLabel( messageSource.getMessage("openMonitor.calibrationGainLabel"));
        JLabel calibrationOffsetLabel = new JLabel( messageSource.getMessage("openMonitor.calibrationOffsetLabel"));

        JLabel pageSizeLabel = new JLabel( messageSource.getMessage("openMonitor.pageSizeLabel"));

        GroupLayout layout = new GroupLayout( this);
        setLayout(layout);
 
        layout.setAutoCreateGaps(true);
 
        layout.setAutoCreateContainerGaps(true);
 
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

        hGroup.addGroup(layout.createParallelGroup()
                 .addComponent( samplingLabel)
                 .addComponent( channelCountLabel)
                 .addComponent( calibrationGainLabel)
                 .addComponent( calibrationOffsetLabel)
                 .addComponent( pageSizeLabel));
        hGroup.addGroup(layout.createParallelGroup()
                 .addComponent( getSamplingField())
                 .addComponent( getChannelCountField())
                 .addComponent( getCalibrationGainField())
                 .addComponent( getCalibrationOffsetField())
                 .addComponent( getPageSizeField()));

        layout.setHorizontalGroup( hGroup);
        
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                addComponent( samplingLabel).addComponent( getSamplingField()));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                addComponent( channelCountLabel).addComponent( getChannelCountField()));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                addComponent( calibrationGainLabel).addComponent( getCalibrationGainField()));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                addComponent( calibrationOffsetLabel).addComponent( getCalibrationOffsetField()));
        vGroup.addGap(50);
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
                addComponent( pageSizeLabel).addComponent( getPageSizeField()));

        layout.setVerticalGroup(vGroup);

	}

    public JTextField getSamplingField() {
        if (samplingField == null) {
            samplingField = new JTextField();
        }
        return samplingField;
    }

    public JTextField getChannelCountField() {
        if (channelCountField == null) {
            channelCountField = new JTextField();
        }
        return channelCountField;
    }

    public JTextField getCalibrationGainField() {
        if (calibrationGainField == null) {
            calibrationGainField = new JTextField();
        }
        return calibrationGainField;
    }

    public JTextField getCalibrationOffsetField() {
        if (calibrationOffsetField == null) {
            calibrationOffsetField = new JTextField();
        }
        return calibrationOffsetField;
    }

    public JTextField getPageSizeField() {
        if (pageSizeField == null) {
            pageSizeField = new JTextField();
        }
        return pageSizeField;
    }

}
