package org.signalml.app.worker;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.signalml.multiplexer.protocol.SvarogConstants;

/**
 *
 */
public class MultiplexerConnectWorker extends SwingWorker< WorkerResult, Integer> {

	protected static final Logger logger = Logger.getLogger( MultiplexerConnectWorker.class);
	
	public static final int TIMEOUT_MILIS = 50;

	private ViewerElementManager elementManager;
	private InetSocketAddress multiplexerSocket;
	private int timeoutMilis = OpenMonitorDialog.TIMEOUT_MILIS;
	private int tryoutCount = OpenMonitorDialog.TRYOUT_COUNT;
	private Integer connectingState = 0;
	private JmxClient client;
	
	public MultiplexerConnectWorker( ViewerElementManager elementManager,
								InetSocketAddress multiplexerSocket,
								Integer timeoutMilis, 
								Integer tryoutCount) {
		this.elementManager = elementManager;
		this.multiplexerSocket = multiplexerSocket;
		if (timeoutMilis != null)
			this.timeoutMilis = timeoutMilis.intValue();
		if (tryoutCount != null)
			this.tryoutCount = tryoutCount.intValue();
	}

	@Override
	protected WorkerResult doInBackground() throws Exception {

		logger.info( "Worker: start...");

		client = new JmxClient( SvarogConstants.PeerTypes.STREAM_RECEIVER);
		ChannelFuture connectFuture = client.asyncConnect( multiplexerSocket);

		int i = 0;
		while (!isCancelled() && i < tryoutCount) {
			i++;
			try {
				Thread.sleep( timeoutMilis);
			}
			catch (InterruptedException e1) {}
			publish( new Integer( i));
			if ((connectFuture.isDone())) {
				break;
			}
		}

		if ( i < tryoutCount)
			publish( new Integer( tryoutCount));

		// timeout!!!
		if (connectFuture.isDone()) {
			if (connectFuture.isSuccess()) {
				logger.info( "Worker: connected!");
				return new WorkerResult( Boolean.TRUE, 
						elementManager.getMessageSource().getMessage( "action.connectingMultiplexer.connectionOkMsg"));
			}
			else {
				logger.error("connection failed!");
				Throwable cause = connectFuture.getCause();
				return new WorkerResult( Boolean.FALSE, 
						elementManager.getMessageSource().getMessage( "action.connectingMultiplexer.connectionFailedMsg") + 
								"; " + cause);
			}
		}
		else {
			logger.error("connection timed out!");
			return new WorkerResult( Boolean.FALSE, 
					elementManager.getMessageSource().getMessage( "action.connectingMultiplexer.connectionTimeoutMsg"));
		}
	}

	@Override
	protected void process( List<Integer> states) {
		for (Integer i : states) {
			Integer oldConnectingState = connectingState;
			connectingState = i;
			firePropertyChange( "connectingState", oldConnectingState, connectingState);
		}
	}

	@Override
	protected void done() {
		WorkerResult result = null;
		try {
			result = get();
		} 
		catch (InterruptedException e) {
			logger.debug("get() interrupted! " + e.getMessage());
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			logger.debug("get() failed! " + e.getMessage());
			e.printStackTrace();
		}
		if (!result.success) {
			try {
				client.shutdown();
				elementManager.setJmxClient( null);
			}
			catch (InterruptedException e) {
				// should never happen
			}
		}
		else {
			elementManager.setJmxClient( client);
		}
		firePropertyChange( "jmxConnection", null, result);
	}

}
