package org.signalml.app.worker;

import java.util.List;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.ChannelFutureGroup;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.model.PagingParameterDescriptor;
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;
import org.signalml.domain.tag.MonitorTag;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.domain.tag.TagStylesGenerator;
import org.signalml.multiplexer.protocol.SvarogConstants.MessageTypes;
import org.signalml.multiplexer.protocol.SvarogProtocol;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;

import com.google.protobuf.ByteString;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorker<Void, Object> {

	protected static final Logger logger = Logger.getLogger(MonitorWorker.class);
	public static final int TIMEOUT_MILIS = 50;
	private static int TS_DIVIDER = 100000;
	private final JmxClient client;
	private final OpenMonitorDescriptor monitorDescriptor;
	private final RoundBufferMultichannelSampleSource sampleSource;
	private final RoundBufferSampleSource timestampsSource;
	private final StyledMonitorTagSet tagSet;
	private volatile boolean finished;

	/**
	 * This object is responsible for recording tags received by this {@link MonitorWorker}.
	 * It is connected to the monitor by an external object using
	 * {@link MonitorWorker#connectTagRecorderWorker(org.signalml.app.worker.TagRecorder)}.
	 */
	private TagRecorder tagRecorderWorker;

	/**
	 * This object is responsible for recording signal received by this {@link MonitorWorker}.
	 * It is connected to the monitor by an external object using
	 * {@link MonitorWorker#connectSignalRecorderWorker(org.signalml.app.worker.SignalRecorderWorker) }.
	 */
	private SignalRecorderWorker signalRecorderWorker;

	public MonitorWorker(JmxClient client, OpenMonitorDescriptor monitorDescriptor, RoundBufferMultichannelSampleSource sampleSource, RoundBufferSampleSource timestampsSource, StyledMonitorTagSet tagSet) {
		this.client = client;
		this.monitorDescriptor = monitorDescriptor;
		this.sampleSource = sampleSource;
		this.timestampsSource = timestampsSource;
		this.tagSet = tagSet;
		this.tagSet.setTs(timestampsSource);
		logger.setLevel((Level) Level.INFO);
	}

	private boolean sendRequest(final int type) {
		final MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
		builder.setType(type);
		final MultiplexerMessage msg = client.createMessage(builder);
		final ChannelFutureGroup operation;
		try {
			operation = client.send(msg, SendingMethod.THROUGH_ONE);
		} catch(NoPeerForTypeException e) {
			logger.error("nobody to send the message to", e);
			return false;
		}
		try {
			operation.await(1000 /* ms */);
		} catch(InterruptedException e) {
			logger.error("interrupted while sending", e);
			return false;
		}
		if(!operation.isSuccess()) {
			logger.error("sending request " + type + " failed", operation.getCause());
			return false;
		}
		return true;
	}

	@Override
	protected Void doInBackground() {

		logger.info("Worker: start...");

		if (!sendRequest(MessageTypes.SIGNAL_STREAMER_START))
			return null;
		logger.info("Worker: sent streaming request!");

		final int channelCount = monitorDescriptor.getChannelCount();
		final int plotCount = monitorDescriptor.getSelectedChannelList().length;
		final int[] selectedChannels = monitorDescriptor.getSelectedChannelsIndecies();
		final double[] gain = monitorDescriptor.getGain();
		final double[] offset = monitorDescriptor.getOffset();
		logger.debug("Worker: got what wanted!");

		final double[] chunk = new double[channelCount];
		//TODO blocksPerPage - is that information sent to the monitor worker? Can we substitute default PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE
		//with a real value?
		final TagStylesGenerator stylesGenerator
			= new TagStylesGenerator(monitorDescriptor.getPageSize(),
						 PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);

		logger.info("Start receiving ...");
		while (!isCancelled()) {
			logger.debug("Worker: receiving!");

			// Receive message
			final IncomingMessageData msgData;
			try {
				msgData = client.receive(TIMEOUT_MILIS);
				if (msgData == null) /* timeout */
					continue;
			} catch (InterruptedException e) {
				logger.error("receive failed", e);
				return null;
			}

			final MultiplexerMessage sampleMsg;
			final ByteString sampleMsgString;

			// Unpack message
			sampleMsg = msgData.getMessage();
			sampleMsgString = sampleMsg.getMessage();

			final int sampleType = sampleMsg.getType();
			logger.debug("Worker: received message type: " + sampleType);

			switch (sampleType){
			case MessageTypes.AMPLIFIER_SIGNAL_MESSAGE:
				logger.debug("Worker: reading chunk!");

				final SampleVector sampleVector;
				try {
					sampleVector = SampleVector.parseFrom(sampleMsgString);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				final List<Sample> samples = sampleVector.getSamplesList();

				//DEBUG
				timestampsSource.addSample(samples.get(0).getTimestamp());
				this.tagSet.newSample(samples.get(0).getTimestamp());
				//DEBUG

				// Get values from samples to chunk
				for (int i = 0; i < channelCount; i++)
					chunk[i] = samples.get(i).getValue();

				// Transform chunk using gain and offset
				double[] condChunk = new double[plotCount];
				double[] selectedChunk = new double[plotCount];
				for (int i = 0; i < plotCount; i++) {
					int n = selectedChannels[i];
					condChunk[i] = gain[n] * chunk[n] + offset[n];
					selectedChunk[i] = chunk[n];
				}

				// set first sample timestamp for the tag recorder
				if (tagRecorderWorker != null && !tagRecorderWorker.isStartRecordingTimestampSet()) {
					tagRecorderWorker.setStartRecordingTimestamp(samples.get(0).getTimestamp());
				}

				// sends chunks to the signal recorder
				if (signalRecorderWorker != null) {
					signalRecorderWorker.offerChunk(selectedChunk.clone());
					if (!signalRecorderWorker.isFirstSampleTimestampSet())
						signalRecorderWorker.setFirstSampleTimestamp(samples.get(0).getTimestamp());
				}

				publish(condChunk);
				break;
			case MessageTypes.TAG:
				logger.info("Tag recorder: got a tag!");

				final SvarogProtocol.Tag tagMsg;
				try {
					tagMsg = SvarogProtocol.Tag.parseFrom(sampleMsgString);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				// Create MonitorTag Object, define its style and attributes

				// String channels = tagMsg.getChannels();
				// By now we ignore field channels and assume that tag if for all channels

				final double tagLen = tagMsg.getEndTimestamp() - tagMsg.getStartTimestamp();
				final MonitorTag tag
					= new MonitorTag(stylesGenerator.getSmartStyleFor(tagMsg.getName(), tagLen, -1),
							 tagMsg.getStartTimestamp(),
							 tagLen,
							 -1);

				/* TODO: temporary disabled monitor attributes
				 for (SvarogProtocol.Variable v : tagMsg.getDesc().getVariablesList())
					tag.setAttribute(v.getKey(), v.getValue());*/

				if(isChannelSelected(tag.getChannel(), selectedChannels)) {
					if (tagRecorderWorker != null) {
						tagRecorderWorker.offerTag(tag);
					}

					publish(tag);
				}
			default:
				final int type = sampleMsg.getType();
				final String name = MessageTypes.instance.getConstantsNames().get(type);
				logger.error("received unknown reply: " +  type + "/" + name);
			}
		}

		logger.debug("stopping server...");
		sendRequest(MessageTypes.SIGNAL_STREAMER_STOP);
		// ignore return value

		return null;
	}

	private boolean isChannelSelected(final int channel, final int selectedChannels[]) {
		if (channel == -1)
			return true;
		for (int selected : selectedChannels)
			if (channel == selected)
				return true;
		return false;
	}

	private double convertTs(double ts) {
		double tmp_s_ts = ((double) ((((int) ts) / this.TS_DIVIDER) * this.TS_DIVIDER));
		double ret_ts = ts - tmp_s_ts;
		return ret_ts;

	}

	@Override
	protected void process(List<Object> objs) {
		for (Object o : objs) {
			if (o instanceof double[]) {
				sampleSource.lock();
				sampleSource.addSamples((double[]) o);
				sampleSource.unlock();
			} else {
				logger.info("got TAG ");
				tagSet.lock();
				tagSet.addTag((MonitorTag) o);
				tagSet.unlock();
				firePropertyChange("newTag", null, (MonitorTag) o);
			}
		}
	}

	@Override
	protected void done() {
		finished = true;
		firePropertyChange("tagsRead", null, tagSet);
	}

	public StyledMonitorTagSet getTagSet() {
		return tagSet;
	}

	public boolean isFinished() {
		return finished;
	}

	/**
	 * Sets the {@link TagRecorder} to which the tags will be sent by this
	 * {@link MonitorWorker}. Setting a {@link TagRecorder} using this method
	 * starts sending all tags received by this {@link MonitorWorker} to the
	 * given {@link TagRecorder}.
	 *
	 * @param tagRecorderWorker the {@link TagRecorder} responsible for recording
	 * the tags from this {@link MonitorWorker}.
	 */
	public void connectTagRecorderWorker(TagRecorder tagRecorderWorker) {
		this.tagRecorderWorker = tagRecorderWorker;
	}

	/**
	 * Allows to disconnect a {@link TagRecorder} which was connected using
	 * {@link MonitorWorker#connectTagRecorderWorker(org.signalml.app.worker.TagRecorder)}.
	 * No more tags are sent to the {@link TagRecorder} after disconnecting.
	 */
	public void disconnectTagRecorderWorker() {
		this.tagRecorderWorker = null;
	}

	/**
	 * Sets the {@link SignalRecorderWorker} to which signal will be sent by this
	 * {@link MonitorWorker}. Setting a {@link SignalRecorderWorker} using this method
	 * starts sending signal received by this {@link MonitorWorker} to the
	 * given SignalRecorderWorker.
	 *
	 * @param signalRecorderWorker the {@link SignalRecorderWorker} responsible for recording
	 * signal from this {@link MonitorWorker}.
	 */
	public void connectSignalRecorderWorker(SignalRecorderWorker signalRecorderWorker) {
		this.signalRecorderWorker = signalRecorderWorker;
	}

	/**
	 * Disconnects a {@link SignalRecorderWorker} which was connected using
	 * {@link MonitorWorker#connectSignalRecorderWorker(org.signalml.app.worker.SignalRecorderWorker) }.
	 * Signal is no longer sent to the {@link SignalRecorderWorker} after disconnecting.
	 */
	public void disconnectSignalRecorderWorker() {
		this.signalRecorderWorker = null;
	}
}
