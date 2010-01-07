/* OpenDocumentStepOnePanel.java created 2007-09-17
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import org.apache.log4j.Logger;
import org.signalml.app.action.ConnectMultiplexerAction;
import org.signalml.app.action.DisconnectMultiplexerAction;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.signalml.app.worker.WorkerResult;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 */
public class MultiplexerConnectionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MultiplexerConnectionPanel.class);
	
    private ViewerElementManager elementManager;

    private JTextField multiplexerAddressField;
    private JTextField multiplexerPortField;

    private JTextArea statusArea;
    private JProgressBar progressBar;
    private JButton connectButton;
    private JButton disonnectButton;
    private ConnectMultiplexerAction connectAction;

	/**
	 * This is the default constructor
	 */
	public MultiplexerConnectionPanel( ViewerElementManager elementManager) {
		super();
        this.elementManager = elementManager;
		initialize();
	}

	protected MessageSourceAccessor getMessageSource() {
	    return elementManager.getMessageSource();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

	    setLayout( new BorderLayout());

        JPanel fieldPanel = new JPanel();
        GroupLayout layout = new GroupLayout(fieldPanel);
        fieldPanel.setLayout(layout);
 
        layout.setAutoCreateGaps(true);
 
        layout.setAutoCreateContainerGaps(true);
 
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        
        JLabel multiplexerAddressLabel = new JLabel( getMessageSource().getMessage( "openMonitor.multiplexerAddressLabel"));
        JLabel multiplexerPortLabel = new JLabel( getMessageSource().getMessage("openMonitor.multiplexerPortLabel"));

        hGroup.addGroup(layout.createParallelGroup()
                 .addComponent(multiplexerAddressLabel)
                 .addComponent(multiplexerPortLabel));
        hGroup.addGroup(layout.createParallelGroup()
                 .addComponent( getMultiplexerAddressField())
                 .addComponent( getMultiplexerPortField()));

        layout.setHorizontalGroup( hGroup);
        
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                 .addComponent(multiplexerAddressLabel)
                 .addComponent( getMultiplexerAddressField()));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
                 .addComponent(multiplexerPortLabel)
                 .addComponent( getMultiplexerPortField()));

        layout.setVerticalGroup(vGroup);

        add( fieldPanel, BorderLayout.CENTER);

        JPanel connectionPanel = new JPanel();
        connectionPanel.setLayout( new GridBagLayout());

        DisconnectMultiplexerAction disconnectAction = new DisconnectMultiplexerAction( elementManager);
        getDisconnectButton().setAction( disconnectAction);
        disconnectAction.addPropertyChangeListener( new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("disconnected".equals( evt.getPropertyName())) {
                    connectAction.setMode( ConnectMultiplexerAction.CONNECT);
                    getConnectButton().setEnabled( true);
                    getProgressBar().setValue( 0);
                    getStatusArea().setText( null);
                }
            }
        });

        final PropertyChangeListener propertyChangeHandler = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("connectingState".equals( evt.getPropertyName())) {
                    Integer state = (Integer) evt.getNewValue();
                    getProgressBar().getModel().setValue( state.intValue());
                }
                else if ("jmxConnection".equals( evt.getPropertyName())) {
                    WorkerResult res = (WorkerResult) evt.getNewValue();
                    getProgressBar().getModel().setValue( new Integer( OpenMonitorDialog.TRYOUT_COUNT));
                    if (!res.success) {
                        getStatusArea().setForeground( OpenMonitorDialog.FAILURE_COLOR);
                        getStatusArea().setText( res.message);
                        connectAction.setMode( ConnectMultiplexerAction.CONNECT);
                    }
                }
                else if ("testState".equals( evt.getPropertyName())) {
                    Integer state = (Integer) evt.getNewValue();
                    getProgressBar().getModel().setValue( state.intValue() + OpenMonitorDialog.TRYOUT_COUNT);
                }
                else if ("connectionTestResult".equals( evt.getPropertyName())) {
                    WorkerResult res = (WorkerResult) evt.getNewValue();
                    if (res.success) {
                        getStatusArea().setForeground( OpenMonitorDialog.SUCCESS_COLOR);
                        getDisconnectButton().setEnabled( true);
                    }
                    else
                        getStatusArea().setForeground( OpenMonitorDialog.FAILURE_COLOR);
                    getConnectButton().setEnabled( false);
                    getStatusArea().setText( res.message);
                    getProgressBar().getModel().setValue( getProgressBar().getModel().getMaximum());
                }
            }
        };
        connectAction = new ConnectMultiplexerAction( elementManager);
        connectAction.setDisconnectAction( disconnectAction);
        connectAction.setMultiplexerAddressField(getMultiplexerAddressField());
        connectAction.setMultiplexerPortField( getMultiplexerPortField());
        connectAction.setTryoutCount( OpenMonitorDialog.TRYOUT_COUNT);
        connectAction.setTimeoutMilis( OpenMonitorDialog.TIMEOUT_MILIS);
        connectAction.addPropertyChangeListener( propertyChangeHandler);
        getConnectButton().setAction( connectAction);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets( 5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        connectionPanel.add( getConnectButton(), c);
        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        connectionPanel.add( getDisconnectButton(), c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        connectionPanel.add( getProgressBar(), c);
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        connectionPanel.add( getStatusArea(), c);

        add( connectionPanel, BorderLayout.SOUTH);

	}

    public JTextField getMultiplexerAddressField() {
        if (multiplexerAddressField == null) {
            multiplexerAddressField = new JTextField();
        }
        return multiplexerAddressField;
    }

    public JTextField getMultiplexerPortField() {
        if (multiplexerPortField == null) {
            multiplexerPortField = new JTextField();
        }
        return multiplexerPortField;
    }

    public JTextArea getStatusArea() {
        if (statusArea == null) {
            statusArea = new JTextArea();
            statusArea.setPreferredSize( new Dimension( 400, 100));
            statusArea.setBorder( BorderFactory.createLineBorder( Color.BLACK));
        }
        return statusArea;
    }

    public JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar( 0, OpenMonitorDialog.TRYOUT_COUNT * 2);
        }
        return progressBar;
    }

    public JButton getConnectButton() {
        if (connectButton == null) {
            connectButton = new JButton();
        }
        return connectButton;
    }

    public JButton getDisconnectButton() {
        if (disonnectButton == null) {
            disonnectButton = new JButton();
        }
        return disonnectButton;
    }

}
