/* OpenDocumentStepOnePanel.java created 2007-09-17
 * 
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.action.ConnectMultiplexerAction;
import org.signalml.app.action.DisconnectMultiplexerAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.signalml.app.worker.WorkerResult;

/**
 *
 */
public class MultiplexerConnectionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MultiplexerConnectionPanel.class);
	
	private ViewerElementManager elementManager;
	private ApplicationConfiguration applicationConfiguration;

	private OpenMonitorDescriptor openMonitorDescriptor;

	private JTextField multiplexerAddressField;
	private JTextField multiplexerPortField;

	private JTextArea statusArea;
	private JProgressBar progressBar;
	private JButton connectButton;
	private JButton disonnectButton;

	/**
	 * Button used to reset to defaults multiplexer address and port values.
	 */
	private JButton resetToDefaultsButton;

	private ConnectMultiplexerAction connectAction;
	private DisconnectMultiplexerAction disconnectAction;

	/**
	 * This is the default constructor
	 */
	public MultiplexerConnectionPanel(ViewerElementManager elementManager) {
		super();
		this.elementManager = elementManager;
		this.applicationConfiguration = elementManager.getApplicationConfig();
		initialize();
	}

	/**
	 * This method initializes components used in this panel.
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Connect to the multiplexer")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		JPanel fieldPanel = new JPanel();
		GroupLayout layout = new GroupLayout(fieldPanel);
		fieldPanel.setLayout(layout);
 
		layout.setAutoCreateGaps(true);
 
		layout.setAutoCreateContainerGaps(true);
 
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		JLabel multiplexerAddressLabel = new JLabel(_("Multiplexer address"));
		JLabel multiplexerPortLabel = new JLabel(_("Multiplexer port"));

		hGroup.addGroup(layout.createParallelGroup()
				 .addComponent(multiplexerAddressLabel)
				 .addComponent(multiplexerPortLabel));
		hGroup.addGroup(layout.createParallelGroup()
				 .addComponent( getMultiplexerAddressField())
				 .addComponent( getMultiplexerPortField()));

		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				 .addComponent(multiplexerAddressLabel)
				 .addComponent( getMultiplexerAddressField()));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				 .addComponent(multiplexerPortLabel)
				 .addComponent( getMultiplexerPortField()));

		layout.setVerticalGroup(vGroup);

		add(fieldPanel, BorderLayout.CENTER);

		JPanel connectionPanel = new JPanel();
		connectionPanel.setLayout( new GridBagLayout());

		disconnectAction = new DisconnectMultiplexerAction(elementManager);
		getDisconnectButton().setAction(disconnectAction);
		disconnectAction.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("disconnected".equals( evt.getPropertyName())) {
					getConnectButton().setEnabled(true);
					getProgressBar().setValue( 0);
					getStatusArea().setText(null);
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
					getProgressBar().getModel().setValue(new Integer(OpenMonitorDialog.TRYOUT_COUNT));
					if (!res.success) {
						getStatusArea().setForeground(OpenMonitorDialog.FAILURE_COLOR);
						getStatusArea().setText(res.message);
					}
				}
				else if ("testState".equals( evt.getPropertyName())) {
					Integer state = (Integer) evt.getNewValue();
					getProgressBar().getModel().setValue(state.intValue() + OpenMonitorDialog.TRYOUT_COUNT);
				}
				else if ("connectionTestResult".equals( evt.getPropertyName())) {
					WorkerResult res = (WorkerResult) evt.getNewValue();
					if (res.success) {
						getStatusArea().setForeground(OpenMonitorDialog.SUCCESS_COLOR);
						getDisconnectButton().setEnabled(true);
					}
					else
						getStatusArea().setForeground(OpenMonitorDialog.FAILURE_COLOR);
					getConnectButton().setEnabled(false);
					getStatusArea().setText(res.message);
					getProgressBar().getModel().setValue( getProgressBar().getModel().getMaximum());
				}
			}
		};
		connectAction = new ConnectMultiplexerAction(elementManager);
		connectAction.setDisconnectAction(disconnectAction);
		connectAction.setMultiplexerAddressField(getMultiplexerAddressField());
		connectAction.setMultiplexerPortField( getMultiplexerPortField());
		connectAction.setTryoutCount(OpenMonitorDialog.TRYOUT_COUNT);
		connectAction.setTimeoutMilis(OpenMonitorDialog.TIMEOUT_MILIS);
		connectAction.addPropertyChangeListener(propertyChangeHandler);
		connectAction.setOpenMonitorDescriptor(openMonitorDescriptor);
		getConnectButton().setAction(connectAction);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.WEST;
		connectionPanel.add(getConnectButton(), c);
		c.gridx = 1;
		connectionPanel.add(getDisconnectButton(), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		connectionPanel.add(getResetToDefaultsButton(), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		connectionPanel.add(getProgressBar(), c);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		connectionPanel.add(getStatusArea(), c);

		add(connectionPanel, BorderLayout.SOUTH);

	}

	public OpenMonitorDescriptor getOpenMonitorDescriptor() {
		return openMonitorDescriptor;
	}

	protected void setOpenMonitorDescriptor(OpenMonitorDescriptor openMonitorDescriptor) {
		this.openMonitorDescriptor = openMonitorDescriptor;
		if (connectAction != null)
			connectAction.setOpenMonitorDescriptor(openMonitorDescriptor);
	}

	public JTextField getMultiplexerAddressField() {
		if (multiplexerAddressField == null) {
			multiplexerAddressField = new JTextField();
			multiplexerAddressField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					applicationConfiguration.setMultiplexerAddress(multiplexerAddressField.getText());
				}
			});
		}
		return multiplexerAddressField;
	}

	public JTextField getMultiplexerPortField() {
		if (multiplexerPortField == null) {
			multiplexerPortField = new JTextField();
			multiplexerPortField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					applicationConfiguration.setMultiplexerPort(Integer.valueOf(multiplexerPortField.getText()));
				}
			});
		}
		return multiplexerPortField;
	}

	public JTextArea getStatusArea() {
		if (statusArea == null) {
			statusArea = new JTextArea();
			statusArea.setPreferredSize(new Dimension(400, 100));
			statusArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		return statusArea;
	}

	public JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(0, OpenMonitorDialog.TRYOUT_COUNT * 2);
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

	/**
	 * Returns button which may be used to reset to defaults multiplexer
	 * address and port fields values.
	 * @return button which can be used to reset connection parameters.
	 */
	public JButton getResetToDefaultsButton() {
		if (resetToDefaultsButton == null) {
			resetToDefaultsButton = new JButton(new AbstractAction(_("Reset to defaults")) {

					@Override
					public void actionPerformed(ActionEvent e) {
						applicationConfiguration.resetMultiplexerAddressToDefaults();
						applicationConfiguration.resetMultiplexerPortToDefaults();
						fillPanelFromModel(openMonitorDescriptor);
					}
				}
			);
		}
		return resetToDefaultsButton;
	}

	public void cancel() {
		if (connectAction != null) {
			connectAction.cancel();
		}
		setInterfaceInUnconnectedState();
	}

	/**
	 * Sets this interface to be in an unconnected state. After calling this
	 * method new connections can be made.
	 */
	public void setInterfaceInUnconnectedState() {
		getConnectButton().setEnabled(true);
		getDisconnectButton().setEnabled(false);
		getProgressBar().setValue(0);
		getStatusArea().setText("");
	}

	/**
	 * Returns the action associated with the Connect button.
	 * @return an action associated with the Connect button.
	 */
	public ConnectMultiplexerAction getConnectAction() {
		return connectAction;
	}

	/**
	 * Returns the action associated with the Disonnect button.
	 * @return an action associated with the Disconnect button.
	 */
	public DisconnectMultiplexerAction getDisconnectAction() {
		return disconnectAction;
	}

	/**
	 * Fills the fields of this panel from the given model.
	 * @param openMonitorDescriptor the model from which this dialog will be
	 * filled.
	 */
	public void fillPanelFromModel(OpenMonitorDescriptor openMonitorDescriptor) {

		String address = openMonitorDescriptor.getMultiplexerAddress();
		int port = openMonitorDescriptor.getMultiplexerPort();

		setOpenMonitorDescriptor(openMonitorDescriptor);

		if (applicationConfiguration != null) {
			address = applicationConfiguration.getMultiplexerAddress();
			port = applicationConfiguration.getMultiplexerPort();
		}

		if (address != null) {
			getMultiplexerAddressField().setText(address);
		}
		if (port != -1) {
			getMultiplexerPortField().setText(Integer.toString(port));
		}

	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param openMonitorDescriptor the model to be filled.
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor openMonitorDescriptor) {

		String adres = getMultiplexerAddressField().getText();
		openMonitorDescriptor.setMultiplexerAddress(adres);
		if (applicationConfiguration != null)
			applicationConfiguration.setMultiplexerAddress(adres);

		int port = Integer.parseInt(getMultiplexerPortField().getText());
		openMonitorDescriptor.setMultiplexerPort(port);
		if (applicationConfiguration != null)
			applicationConfiguration.setMultiplexerPort(port);

	}

	/**
	 * Sets the {@link ApplicationConfiguration} for this panel.
	 * @param applicationConfiguration a Svarog configuration to be used with
	 * this panel to get the default values for some fields.
	 */
	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

}
