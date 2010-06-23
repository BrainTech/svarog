package org.signalml.app.worker;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Constants;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.springframework.context.support.MessageSourceAccessor;

import com.google.protobuf.ByteString;

/** 
 *
 */
public class MultiplexerConnectionTestWorker extends SwingWorker< WorkerResult, Integer> {

	protected static final Logger logger = Logger.getLogger( MultiplexerConnectionTestWorker.class);

	private MessageSourceAccessor messageSource;
	private int timeoutMilis = OpenMonitorDialog.TIMEOUT_MILIS;
	private int tryoutCount = OpenMonitorDialog.TRYOUT_COUNT;
	private Integer testState = tryoutCount;
	private JmxClient client;

	public MultiplexerConnectionTestWorker( MessageSourceAccessor messageSource,
									JmxClient client,
									Integer timeoutMilis, 
									Integer tryoutCount) {
		this.messageSource = messageSource;
		this.client = client;
		if (timeoutMilis != null)
			this.timeoutMilis = timeoutMilis.intValue();
		if (tryoutCount != null)
			this.tryoutCount = tryoutCount.intValue();
	}

	@Override
	protected WorkerResult doInBackground() throws Exception {

		logger.info("Testing multiplexer concection...");

		int i = 0;

		// create message
		ByteString msgBody = ByteString.copyFromUtf8( "Testing multiplexer connection...");
		MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
		builder.setType( Constants.MessageTypes.PING).setMessage( msgBody);
		MultiplexerMessage msg = client.createMessage( builder);

		// send message
		try {
			ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
			sendingOperation.await(1, TimeUnit.SECONDS);
			if (!sendingOperation.isSuccess()) {
				logger.info("sending failed!");
				return new WorkerResult( 
						Boolean.FALSE, 
						messageSource.getMessage( 
								"action.openMonitor.testMultiplexerConnection.sendingFailedMsg"));
			}
		}
		catch (NoPeerForTypeException e) {
			logger.error("sending failed! " + e.getMessage());
			return new WorkerResult( 
					Boolean.FALSE, 
					messageSource.getMessage( 
							"action.openMonitor.testMultiplexerConnection.sendingFailedMsg") + "; " + e.getMessage());
		} 
		catch (InterruptedException e) {
			logger.error("sending failed! " + e.getMessage());
			return new WorkerResult( 
					Boolean.FALSE, 
					messageSource.getMessage( 
							"action.openMonitor.testMultiplexerConnection.sendingFailedMsg") + "; " + e.getMessage());
		}

		while (!isCancelled() && i < tryoutCount) {

			logger.debug( "Worker: receiving!");

			// receive message
			IncomingMessageData msgData = null;
			try {
				msgData = client.receive( timeoutMilis);
				if (msgData != null) {
					publish( tryoutCount);
					MultiplexerMessage reply = msgData.getMessage();
					if (!(reply.getType() == msg.getType() && reply.getMessage().equals( msg.getMessage()))) {
						logger.error("received bad reply! " + reply.getMessage());
						return new WorkerResult( 
								Boolean.TRUE, 
								messageSource.getMessage( 
										"action.openMonitor.testMultiplexerConnection.receivedBadReplyMsg") + "; " + reply.getMessage());
					}
					return new WorkerResult( 
							Boolean.TRUE, 
							messageSource.getMessage( 
									"action.openMonitor.testMultiplexerConnection.connectionOkMsg"));
				}
			} 
			catch (InterruptedException e) {
				logger.error("receiveing failed! " + e.getMessage());
				return new WorkerResult(
						Boolean.FALSE, 
						messageSource.getMessage( 
								"action.openMonitor.testMultiplexerConnection.receivingFailedMsg") + "; " + e.getMessage());
			}
		}

		logger.info("receive timed out!");
		return new WorkerResult( 
				Boolean.FALSE,
				messageSource.getMessage( 
						"action.openMonitor.testMultiplexerConnection.receiveTimedout") + "; ");
	}

	@Override
	protected void process( List<Integer> states) {
		for (Integer i : states) {
			Integer oldTestState = testState;
			testState = i;
			firePropertyChange( "testState", oldTestState, testState);
		}
	}

	@Override
	protected void done() {
		WorkerResult result = null;
		try {
			result = get();
		} 
		catch (InterruptedException e) {
			logger.debug("get interrupted! " + e.getMessage());
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			logger.debug("get failed! " + e.getMessage());
			e.printStackTrace();
		}
		firePropertyChange( "connectionTestResult", null, result);
	}

}
