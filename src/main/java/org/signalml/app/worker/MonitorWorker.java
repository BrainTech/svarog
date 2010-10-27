package org.signalml.app.worker;

import java.awt.Color;
import java.util.List;
import java.lang.Float;
import java.lang.Double;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.domain.tag.MonitorTag;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.domain.tag.TagStylesGenerator;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;

import com.google.protobuf.ByteString;
import org.signalml.app.model.PagingParameterDescriptor;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorker< Void, Object> {

	protected static final Logger logger = Logger.getLogger(MonitorWorker.class);

	public static final int TIMEOUT_MILIS = 50;
	private static int TS_DIVIDER = 100000;

	private JmxClient jmxClient;
	private OpenMonitorDescriptor monitorDescriptor;
	private LinkedBlockingQueue< double[]> sampleQueue;
	private RoundBufferMultichannelSampleSource sampleSource;
	private RoundBufferSampleSource timestampsSource;
	private StyledMonitorTagSet tagSet;
	private volatile boolean finished;


	public MonitorWorker(JmxClient jmxClient, OpenMonitorDescriptor monitorDescriptor, RoundBufferMultichannelSampleSource sampleSource, RoundBufferSampleSource timestampsSource, StyledMonitorTagSet tagSet) {
		this.jmxClient = jmxClient;
		this.monitorDescriptor = monitorDescriptor;
		this.sampleSource = sampleSource;
		this.timestampsSource = timestampsSource;
		this.tagSet = tagSet;
		this.tagSet.setTs(timestampsSource);
		logger.setLevel((Level) Level.INFO);
	}

	public LinkedBlockingQueue<double[]> getSampleQueue() {
		return sampleQueue;
	}

	public void setSampleQueue(LinkedBlockingQueue<double[]> sampleQueue) {
		this.sampleQueue = sampleQueue;
	}

	@Override
	protected Void doInBackground() throws Exception {

		logger.info("Worker: start...");


		MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
		builder.setType(SvarogConstants.MessageTypes.SIGNAL_STREAMER_START);

		MultiplexerMessage msg = jmxClient.createMessage(builder);

		// send message
		try {
			ChannelFuture sendingOperation = jmxClient.send(msg, SendingMethod.THROUGH_ONE);
			sendingOperation.await(1, TimeUnit.SECONDS);
			if (!sendingOperation.isSuccess()) {
				logger.error("sending failed!");
				return null;
			}
		}
		catch (NoPeerForTypeException e) {
			logger.error("sending failed! " + e.getMessage());
			return null;
		}
		catch (InterruptedException e) {
			logger.error("sending failed! " + e.getMessage());
			return null;
		}

		logger.info("Worker: sent streaming request!");
		int channelCount = monitorDescriptor.getChannelCount();
		int plotCount = monitorDescriptor.getSelectedChannelList().length;
		int[] selectedChannels = monitorDescriptor.getSelectedChannelsIndecies();
		double[] gain = monitorDescriptor.getGain();
		double[] offset = monitorDescriptor.getOffset();
		logger.info("Worker: got what wanted!");

		//DEBUG
		SvarogProtocol.Tag xxx_tagMsg = null;
		int xxx_debug = 0;//just print timestamp of current tag and sample when got tag
		//xxx_debu = 2 - modify first channel when got tag
		//DEBUG

		IncomingMessageData msgData = null;
		MultiplexerMessage sampleMsg = null;
		ByteString sampleMsgString;
		SampleVector sampleVector = null;
		SvarogProtocol.Tag tagMsg = null;

		List<Sample> samples;
		double[] chunk = new double[channelCount];
		//TODO blocksPerPage - is that information sent to the monitor worker? Can we substitute default PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE
		//with a real value?
		TagStylesGenerator stylesGenerator = new TagStylesGenerator(monitorDescriptor.getPageSize(), PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);
		int sampleType = 0;
		Tag tag;
		double tagLen;
		SvarogProtocol.VariableVector tagDesc;
		logger.info("Start receiving ...");
		while (!isCancelled()) {

			logger.debug("Worker: receiving!");
			try {
				// Receive message
				msgData = jmxClient.receive(TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
				if (msgData == null)
					continue;
				// Unpack message
				sampleMsg = msgData.getMessage();
				sampleMsgString = sampleMsg.getMessage();
				sampleType = sampleMsg.getType();
				logger.debug("Worker: received message type: " + sampleType);
				
				
				//DEBUG
				if (sampleType == SvarogConstants.MessageTypes.TAG && xxx_debug > 0) {
				    xxx_tagMsg = SvarogProtocol.Tag.parseFrom(sampleMsgString);
				}
				//DEBUG


				if (sampleType == SvarogConstants.MessageTypes.STREAMED_SIGNAL_MESSAGE) {
					logger.debug("Worker: reading chunk!");

					try {
						sampleVector = SampleVector.parseFrom(sampleMsgString);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					samples = sampleVector.getSamplesList();

					//DEBUG
					timestampsSource.addSample(samples.get(0).getTimestamp());
					this.tagSet.newSample(samples.get(0).getTimestamp());
					//DEBUG
					
					// Get values from samples to chunk
					for (int i = 0; i < channelCount; i++) {
						chunk[i] = samples.get(i).getValue();		
					};

					// Send chunk to recorder
					if (sampleQueue != null)
						sampleQueue.offer(chunk.clone());

					// Transform chunk using gain and offset
					double[] condChunk = new double[plotCount];
					for (int i = 0; i < plotCount; i++) {
						int n = selectedChannels[i];
						condChunk[i] = gain[n] * chunk[n] + offset[n];
					}
					
					
					//DEBUG
					if (xxx_debug > 1 && xxx_tagMsg != null) {
					for (int i = 0; i < plotCount; i++) {					
						double a = condChunk[i];
					    condChunk[i] = 0.0;
					    double b = condChunk[i];
						logger.info("Zmieniam chunka z"+a+" na "+b);
					};
					};
					if (xxx_debug > 0 && xxx_tagMsg != null) {
					    double y = xxx_tagMsg.getStartTimestamp();
					    logger.info("Received tag VS sample timestamp: "+y+" VS "+samples.get(0).getTimestamp());
					    //xxx_tagMsg = null;
					};
					//DEBUG
					
					
					publish(condChunk);
				}
				else if (sampleMsg.getType() == SvarogConstants.MessageTypes.TAG) {

					logger.info("Tag recorder: got a tag!");
					try {
						tagMsg = SvarogProtocol.Tag.parseFrom(sampleMsgString);
					}
					catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					
					// Create MonitorTag Object, define its style and attributes
					
					// String channels = tagMsg.getChannels();
					// By now we ignore field channels and assume that tag if for all channels
					
					tagLen = tagMsg.getEndTimestamp() - tagMsg.getStartTimestamp();
					tag = new MonitorTag(stylesGenerator.getSmartStyleFor(tagMsg.getName(), tagLen, -1),
							tagMsg.getStartTimestamp(),
							tagLen,							  
							-1);

					for (SvarogProtocol.Variable v : tagMsg.getDesc().getVariablesList())
						tag.setAttribute(v.getKey(), v.getValue());
									
					publish(tag);
					logger.info("ILE MAM TAGOW: "+tagSet.getTagCount());

				} else {
					logger.error("received bad reply! " + sampleMsg.getMessage());
					logger.debug("Worker: receive failed!");
					return null;
				}

			}
			catch (InterruptedException e) {
				logger.error("receiveing failed! " + e.getMessage());
				return null;
			}
		}

		logger.debug("Worker: stopping serwer...");

		builder = MultiplexerMessage.newBuilder();
		builder.setType(SvarogConstants.MessageTypes.SIGNAL_STREAMER_STOP);
		msg = jmxClient.createMessage(builder);

		// send message
		try {
			ChannelFuture sendingOperation = jmxClient.send(msg, SendingMethod.THROUGH_ONE);
			sendingOperation.await(1, TimeUnit.SECONDS);
			if (!sendingOperation.isSuccess()) {
				logger.error("sending failed!");
				return null;
			}
		}
		catch (NoPeerForTypeException e) {
			logger.error("sending failed! " + e.getMessage());
			return null;
		}
		catch (InterruptedException e) {
			logger.error("sending failed! " + e.getMessage());
			return null;
		}

		return null;
	}

	private double convertTs(double ts) {
	    double tmp_s_ts = ((double) ((((int) ts)/this.TS_DIVIDER)*this.TS_DIVIDER));
	    double ret_ts = ts - tmp_s_ts;
	    return ret_ts;

	}
	@Override
	protected void process(List< Object> objs) {
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

}
