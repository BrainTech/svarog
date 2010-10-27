package org.signalml.app.worker;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;

import com.google.protobuf.ByteString;

/** 
 *
 */
public class BCIMetadataWorker extends SwingWorker< OpenMonitorDescriptor, Integer> {

	protected static final Logger logger = Logger.getLogger( BCIMetadataWorker.class);

	public static final String SAMPLING_RATE	  = "SamplingRate";
	public static final String NUMBER_OF_CHANNELS = "NumOfChannels";
	public static final String AMPLIFIER_CHANNELS = "AmplifierChannelsToRecord";
	public static final String CHANNEL_NAMES	  = "ChannelsNames";
	public static final String CALIBRATION_GAIN   = "Gain";
	public static final String CALIBRATION_OFFSET = "Offset";
	public static final String MINIMUM_VALUE	  = "MinData";
	public static final String MAXIMUM_VALUE	  = "MaxData";
	public static final String AMPLIFIER_NULL     = "AmplifierNull";

	private MessageSourceAccessor messageSource;
	private JmxClient client;
	private OpenMonitorDescriptor openMonitorDescriptor;
	private int timeout;
	private int state;

	public BCIMetadataWorker( 
			MessageSourceAccessor messageSource, 
			JmxClient client, 
			OpenMonitorDescriptor openMonitorDescriptor, 
			int timeout) {
		this.messageSource = messageSource;
		this.client = client;
		this.openMonitorDescriptor = openMonitorDescriptor;
		this.timeout = timeout;
	}

	/**
	 * Ask multiplexer for dataId, return value for that key, or null when no key or problems
	 * with connection/protocol.
	 * @param dataId A string representing key for data we want to ask for
	 * @param failMsg A message prefix to be sent to monitor descriptor when connection/protocol
	 * problems occur
	 * @return 
	 */
	protected String queryMetaData(String dataId, String failMsg) {
		logger.info("Sending "+dataId+" request...");

		// create message
		MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
		ByteString msgBody = ByteString.copyFromUtf8(dataId);
		builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage(msgBody);
		MultiplexerMessage msg = client.createMessage(builder);

		// send message
		try {
			ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
			sendingOperation.await(1, TimeUnit.SECONDS);
			if (!sendingOperation.isSuccess()) {
				logger.info("sending "+dataId+" request failed!");
				String info = messageSource.getMessage( 
						failMsg+".sendingFailedMsg");
				openMonitorDescriptor.setMetadataInfo( info);
				return null;
			}
		}
		catch (NoPeerForTypeException e) {
			logger.error("sending failed! " + e.getMessage());
			String info = messageSource.getMessage( 
					failMsg+".sendingFailedMsg");
			openMonitorDescriptor.setMetadataInfo( info);
			return null;
		}
		catch (InterruptedException e) {
			logger.error("sending failed! " + e.getMessage());
			String info = messageSource.getMessage( 
					failMsg+".sendingFailedMsg");
			openMonitorDescriptor.setMetadataInfo( info);
			return null;
		}


		logger.debug( "Receiving "+dataId+"...");

		// receive message
		IncomingMessageData msgData = null;
		try {
			msgData = client.receive(timeout);
			if (msgData != null) {
				MultiplexerMessage reply = msgData.getMessage();
				if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
					logger.error("received bad reply! " + reply.getMessage());
					String info = messageSource.getMessage( 
							failMsg+".receivedBadReplyMsg");
					openMonitorDescriptor.setMetadataInfo( info);
					return null;
				}
				else {
					ByteString bs = reply.getMessage();
					String val = bs.toStringUtf8();
					return val;
				}
			}
			else {
				logger.info("receive timed out!");
				String info = messageSource.getMessage( 
						failMsg+".receiveTimedout");
				openMonitorDescriptor.setMetadataInfo( info);
				return null;
			}
		} 
		catch (InterruptedException e) {
			logger.error("receiveing failed! " + e.getMessage());
			String info = messageSource.getMessage( 
					failMsg+".receivingFailedMsg");
			openMonitorDescriptor.setMetadataInfo( info);
			return null;
		}
		
	}
	@Override
	protected OpenMonitorDescriptor doInBackground() throws Exception {

		String value;
		int step = 0;
		int channelCount = -1;
		
		logger.info("Gathering metadata...");

		// sampling freq
		value = queryMetaData(SAMPLING_RATE, "action.openMonitor.metadataWorker.samplingRate");
		if (value == null)
			return openMonitorDescriptor;
		Float freq = new Float(value);
		openMonitorDescriptor.setSamplingFrequency(freq);
		publish(++step);

		// channel count
		value = queryMetaData(NUMBER_OF_CHANNELS, "action.openMonitor.metadataWorker.channelCount");
		if (value == null)
			return openMonitorDescriptor;
		channelCount = Integer.parseInt(value);
		openMonitorDescriptor.setChannelCount(new Integer(channelCount));
		publish(++step);

		// amplifier channels
		value = queryMetaData(AMPLIFIER_CHANNELS, "action.openMonitor.metadataWorker.amplifierChannels");
		if (value == null)
			return openMonitorDescriptor;
		StringTokenizer st = new StringTokenizer(value, " ");
		int[] amplifierChannels = new int[channelCount];
		for (int i=0; i<channelCount && st.hasMoreTokens(); i++) {
			String s = st.nextToken();
			amplifierChannels[i] = Integer.parseInt(s);
		}
		openMonitorDescriptor.setAmplifierChannels(amplifierChannels);
		publish(++step);

		// channel labels
		value = queryMetaData(CHANNEL_NAMES, "action.openMonitor.metadataWorker.channelNames");
		if (value == null)
			return openMonitorDescriptor;
		StringTokenizer st2 = new StringTokenizer(value, ";");
		String[] channelLabels = new String[channelCount];
		for (int i=0; i<channelCount && st2.hasMoreTokens(); i++)
			channelLabels[i] = st2.nextToken();
		openMonitorDescriptor.setChannelLabels(channelLabels);
		publish(++step);

		// calibration gain
		value = queryMetaData(CALIBRATION_GAIN, "action.openMonitor.metadataWorker.calibrationGain");
		if (value == null)
			return openMonitorDescriptor;
		StringTokenizer st3 = new StringTokenizer(value, " ");
		float[] gain = new float[channelCount];
		for (int i=0; i<channelCount && st3.hasMoreTokens(); i++) {
			String s = st3.nextToken();
			gain[i] = Float.parseFloat(s);
		}		
		openMonitorDescriptor.setCalibrationGain(gain);
		publish( ++step);

		// calibration offset
		value = queryMetaData(CALIBRATION_OFFSET, "action.openMonitor.metadataWorker.calibrationOffset");
		if (value == null)
			return openMonitorDescriptor;
		StringTokenizer st4 = new StringTokenizer(value, " ");
		float[] offset = new float[channelCount];
		for (int i=0; i<channelCount && st4.hasMoreTokens(); i++) {
			String s = st4.nextToken();
			offset[i] = Float.parseFloat(s);
		}
		openMonitorDescriptor.setCalibrationOffset(offset);
		publish(++step);

		// minimum value
		value = queryMetaData(MINIMUM_VALUE, "action.openMonitor.metadataWorker.minimumValue");
		if (value == null)
			return openMonitorDescriptor;
		float minVal = Float.parseFloat(value);
		openMonitorDescriptor.setMinimumValue( minVal);
		publish(++step);

		// maximum value
		value = queryMetaData(MAXIMUM_VALUE, "action.openMonitor.metadataWorker.maximumValue");
		if (value == null)
			return openMonitorDescriptor;
		float maxVal = Float.parseFloat(value);
		openMonitorDescriptor.setMaximumValue(maxVal);
		publish(++step);
		
		// amplifier null value
		value = queryMetaData(AMPLIFIER_NULL, "action.openMonitor.metadataWorker.amplifierNull");
		if (value == null)
			return openMonitorDescriptor;
		double ampNull = Double.parseDouble(value);
		openMonitorDescriptor.setAmplifierNull(ampNull);
		publish(++step);

		String info = messageSource.getMessage( 
				"action.openMonitor.metadataWorker.receivedMetadata");
		openMonitorDescriptor.setMetadataReceived( true);
		openMonitorDescriptor.setMetadataInfo(info);
		return openMonitorDescriptor;

	}

	@Override
	protected void process( List<Integer> states) {
		for (Integer i : states) {
			Integer oldState = state;
			state = i;
			firePropertyChange( "testState", oldState, state);
		}
	}

	@Override
	protected void done() {
		OpenMonitorDescriptor result = null;
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
		firePropertyChange( "metadataRetrieved", null, result);
	}

}
