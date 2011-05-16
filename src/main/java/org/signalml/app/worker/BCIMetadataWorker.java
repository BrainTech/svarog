package org.signalml.app.worker;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.exceptions.JmxException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.multiplexer.protocol.SvarogConstants.MessageTypes;
import org.springframework.context.support.MessageSourceAccessor;

import com.google.protobuf.ByteString;

/** 
 *
 */
public class BCIMetadataWorker extends SwingWorker< OpenMonitorDescriptor, Integer> {

	protected static final Logger logger = Logger.getLogger( BCIMetadataWorker.class);

        public static final String METADATA_RECEIVED = "metadataRetrieved";

	public static final String SAMPLING_RATE	  = "SamplingRate";
	public static final String NUMBER_OF_CHANNELS = "NumOfChannels";
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
	protected String queryMetaData(String dataId) {
		logger.debug("Requesting " + dataId);

		// create message
		final ByteString question = ByteString.copyFromUtf8(dataId);
		final IncomingMessageData reply;

		try {
			reply = client.query(question, MessageTypes.DICT_GET_REQUEST_MESSAGE, 1000 /* ms */);
		} catch(JmxException e) {
			logger.error("request " + dataId + ": " + e.getMessage());
			String info = messageSource.getMessage("action.openMonitor.metadataWorker.communicationFailedMsg");
			openMonitorDescriptor.setMetadataInfo(info);
			return null;
		}

		final MultiplexerMessage message = reply.getMessage();
		assert message.getType() == MessageTypes.DICT_GET_RESPONSE_MESSAGE;
		final String val = message.getMessage().toStringUtf8();
		logger.debug("Received " + dataId + "=" + val);
		return val;
	}

	@Override
	protected OpenMonitorDescriptor doInBackground() throws Exception {

		String value;
		int step = 0;
		int channelCount = -1;
		
		logger.info("Gathering metadata...");

		// sampling freq
		value = queryMetaData(SAMPLING_RATE);
		if (value == null)
			return openMonitorDescriptor;
		Float freq = new Float(value);
		openMonitorDescriptor.setSamplingFrequency(freq);
		publish(++step);

		// channel count
		value = queryMetaData(NUMBER_OF_CHANNELS);
		if (value == null)
			return openMonitorDescriptor;
		channelCount = Integer.parseInt(value);
		openMonitorDescriptor.setChannelCount(new Integer(channelCount));
		publish(++step);

		// channel labels
		value = queryMetaData(CHANNEL_NAMES);
		if (value == null)
			return openMonitorDescriptor;
		StringTokenizer st2 = new StringTokenizer(value, ";");
		String[] channelLabels = new String[channelCount];
		for (int i=0; i<channelCount && st2.hasMoreTokens(); i++)
			channelLabels[i] = st2.nextToken();
		openMonitorDescriptor.setChannelLabels(channelLabels);
		publish(++step);

		// calibration gain
		value = queryMetaData(CALIBRATION_GAIN);
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
		value = queryMetaData(CALIBRATION_OFFSET);
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
		value = queryMetaData(MINIMUM_VALUE);
		if (value == null)
			return openMonitorDescriptor;
		float minVal = Float.parseFloat(value);
		openMonitorDescriptor.setMinimumValue( minVal);
		publish(++step);

		// maximum value
		value = queryMetaData(MAXIMUM_VALUE);
		if (value == null)
			return openMonitorDescriptor;
		float maxVal = Float.parseFloat(value);
		openMonitorDescriptor.setMaximumValue(maxVal);
		publish(++step);
		
		// amplifier null value
		value = queryMetaData(AMPLIFIER_NULL);
		if (value == null)
			return openMonitorDescriptor;
		double ampNull = Double.parseDouble(value);
		openMonitorDescriptor.setAmplifierNull(ampNull);
		publish(++step);

		String info = messageSource.getMessage("action.openMonitor.metadataWorker.receivedMetadata");
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
		firePropertyChange( METADATA_RECEIVED, null, result);
	}

}
