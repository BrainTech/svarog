package org.signalml.app.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetSocketAddress;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.worker.BCIMetadataWorker;
import org.signalml.app.worker.MultiplexerConnectionTestWorker;
import org.signalml.app.worker.MultiplexerConnectWorker;
import org.signalml.app.worker.MultiplexerTagConnectWorker;
import org.signalml.app.worker.WorkerResult;

public class ConnectMultiplexerAction extends AbstractAction implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private ViewerElementManager elementManager;
	private OpenMonitorDescriptor openMonitorDescriptor;
	private JTextField multiplexerAddressField;
	private JTextField multiplexerPortField;
	private Integer timeoutMilis;
	private Integer tryoutCount;
	private DisconnectMultiplexerAction  disconnectAction;
	private MultiplexerConnectWorker connectWorker;
	private MultiplexerTagConnectWorker tagConnectWorker;
	private MultiplexerConnectionTestWorker testWorker;
	private BCIMetadataWorker metadataWorker;
	private InetSocketAddress multiplexerSocket;

	protected static final Logger logger = Logger.getLogger(ConnectMultiplexerAction.class);

	public ConnectMultiplexerAction( ViewerElementManager elementManager) {
		this.elementManager = elementManager;
		this.putValue( NAME, 
					   elementManager.getMessageSource().getMessage( 
							   "action.connectMultiplexer.actionName"));
	}

	public OpenMonitorDescriptor getOpenMonitorDescriptor() {
		return openMonitorDescriptor;
	}

	public void setOpenMonitorDescriptor(OpenMonitorDescriptor openMonitorDescriptor) {
		this.openMonitorDescriptor = openMonitorDescriptor;
	}

	public JTextField getMultiplexerAddressField() {
		return multiplexerAddressField;
	}

	public void setMultiplexerAddressField(JTextField multiplexerAddressField) {
		this.multiplexerAddressField = multiplexerAddressField;
	}

	public JTextField getMultiplexerPortField() {
		return multiplexerPortField;
	}

	public void setMultiplexerPortField(JTextField multiplexerPortField) {
		this.multiplexerPortField = multiplexerPortField;
	}

	public Integer getTimeoutMilis() {
		return timeoutMilis;
	}

	public void setTimeoutMilis(Integer timeoutMilis) {
		this.timeoutMilis = timeoutMilis;
	}

	public Integer getTryoutCount() {
		return tryoutCount;
	}

	public void setTryoutCount(Integer tryoutCount) {
		this.tryoutCount = tryoutCount;
	}

	public DisconnectMultiplexerAction getDisconnectAction() {
		return disconnectAction;
	}

	public void setDisconnectAction(DisconnectMultiplexerAction disconnectAction) {
		this.disconnectAction = disconnectAction;
	}

	@Override
	public synchronized void actionPerformed(ActionEvent ev) {

		logger.info("Connecting multiplexer...");
		
		createSocket();
		executeConnect( multiplexerSocket);
	}

	protected synchronized void createSocket() {

		String multiplexerAddress = null;
		Integer multiplexerPort = null;

		try {
			multiplexerAddress = multiplexerAddressField.getText();
			multiplexerPort = Integer.parseInt( multiplexerPortField.getText());
			multiplexerSocket = new InetSocketAddress( multiplexerAddress, multiplexerPort.intValue());
		} 
		catch (NumberFormatException e) {
			logger.error("bad port number! " + e.getMessage());
			WorkerResult result = new WorkerResult( Boolean.FALSE, 
					elementManager.getMessageSource().getMessage( 
							"action.connectingMultiplexer.badPortNumberMsg"));
			firePropertyChange( "connectionTestResult", null, result);
			return;
		}
		catch (IllegalArgumentException e) {
			logger.error("bad address! " + e.getMessage());
			WorkerResult result = new WorkerResult( Boolean.FALSE, 
					elementManager.getMessageSource().getMessage( 
							"action.connectingMultiplexer.badAddressMsg"));
			firePropertyChange( "connectionTestResult", null, result);
			return;
		}
	}

	protected synchronized void executeConnect( InetSocketAddress multiplexerSocket) {
		connectWorker = new MultiplexerConnectWorker( 
				elementManager, 
				multiplexerSocket, 
				timeoutMilis, 
				tryoutCount);
		connectWorker.addPropertyChangeListener( this);
		connectWorker.execute();
	}

	protected synchronized void executeTest() {
		testWorker = new MultiplexerConnectionTestWorker( 
				elementManager.getMessageSource(), 
				elementManager.getJmxClient(), 
				timeoutMilis, 
				tryoutCount);
		testWorker.addPropertyChangeListener( this);
		testWorker.execute();
	}

	protected synchronized void executeTagConnect( InetSocketAddress multiplexerSocket) {
		tagConnectWorker = new MultiplexerTagConnectWorker( 
				elementManager, 
				multiplexerSocket, 
				timeoutMilis, 
				tryoutCount);
		tagConnectWorker.addPropertyChangeListener( this);
		tagConnectWorker.execute();
	}

	protected synchronized void executeMetadata() {
		metadataWorker = new BCIMetadataWorker( 
				elementManager.getMessageSource(), 
				elementManager.getJmxClient(), 
				openMonitorDescriptor,
				timeoutMilis * tryoutCount);
		metadataWorker.addPropertyChangeListener( this);
		metadataWorker.execute();
	}

	public synchronized void cancel() {
		if (metadataWorker != null) {
			metadataWorker.cancel( false);
		}
		if (testWorker != null) {
			testWorker.cancel( false);
		}
		if (connectWorker != null) {
			connectWorker.cancel( false);
		}
		JmxClient jmxClient = elementManager.getJmxClient();
		if (jmxClient != null) {
			try {
				jmxClient.shutdown();
				elementManager.setJmxClient( null);
			}
			catch (Exception e) {}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange( evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		if ("jmxConnection".equals( evt.getPropertyName())) {
			WorkerResult res = (WorkerResult) evt.getNewValue();
			if (res.success)
				executeTest();
		}
		if ("connectionTestResult".equals( evt.getPropertyName())) {
			WorkerResult res = (WorkerResult) evt.getNewValue();
			if (res.success)
				executeMetadata();
		}
		else if ("metadataRetrieved".equals( evt.getPropertyName())) {
			OpenMonitorDescriptor omd = (OpenMonitorDescriptor) evt.getNewValue();
			if (omd != null && omd.isMetadataReceived())
				disconnectAction.setEnabled(true);
		}
	}

}
