package org.signalml.app.worker;

import java.awt.Color;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;

import com.google.protobuf.ByteString;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorker< Void, Object> {

	protected static final Logger logger = Logger.getLogger( MonitorWorker.class);

	public static final int TIMEOUT_MILIS = 50;

	private JmxClient jmxClient;
	private OpenMonitorDescriptor monitorDescriptor;
	private LinkedBlockingQueue< double[]> sampleQueue;
	private RoundBufferMultichannelSampleSource sampleSource;
	private StyledTagSet tagSet = new StyledTagSet();
	private volatile boolean finished;

	public MonitorWorker( JmxClient jmxClient, OpenMonitorDescriptor monitorDescriptor, RoundBufferMultichannelSampleSource sampleSource) {
		this.jmxClient = jmxClient;
		this.monitorDescriptor = monitorDescriptor;
		this.sampleSource = sampleSource;
	}

	public LinkedBlockingQueue<double[]> getSampleQueue() {
		return sampleQueue;
	}

	public void setSampleQueue(LinkedBlockingQueue<double[]> sampleQueue) {
		this.sampleQueue = sampleQueue;
	}

	@Override
	protected Void doInBackground() throws Exception {

		logger.debug( "Worker: start...");


		MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
		builder.setType( SvarogConstants.MessageTypes.SIGNAL_STREAMER_START);

		MultiplexerMessage msg = jmxClient.createMessage(builder);

		// send message
		try {
			ChannelFuture sendingOperation = jmxClient.send( msg, SendingMethod.THROUGH_ONE);
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

		logger.debug( "Worker: sent streaming request!");

		int channelCount = monitorDescriptor.getChannelCount();
		int plotCount = monitorDescriptor.getSelectedChannelList().length;
		int[] selectedChannels = monitorDescriptor.getSelectedChannelsIndecies();
		double[] gain = monitorDescriptor.getGain();
		double[] offset = monitorDescriptor.getOffset();

//		PrintWriter out = new PrintWriter( new File( "recv_data.tsv"));

		while (!isCancelled()) {

			logger.debug( "Worker: receiving!");
			// receive message
			IncomingMessageData msgData = null;
			try {
				msgData = jmxClient.receive( TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
				if (msgData == null){
                                            logger.debug("Received null msgData");
					continue;
                                }
				MultiplexerMessage sampleMsg = msgData.getMessage();
				int type = sampleMsg.getType();
				logger.debug( "Worker: received message type: " + type);
				
				if (sampleMsg.getType() == SvarogConstants.MessageTypes.STREAMED_SIGNAL_MESSAGE) {
					logger.debug( "Worker: reading chunk!");

					ByteString msgString = sampleMsg.getMessage();
					SampleVector sampleVector = null;
					try {
						sampleVector = SampleVector.parseFrom( msgString);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					List<Sample> samples = sampleVector.getSamplesList();

//					String s = Double.toString( samples.get(0).getTimestamp());
//					s = s.replace( '.', ',');
//					out.print( s);

					// wyciągnięcie chunka do tablicy double z sampli
					double[] chunk = new double[channelCount];
					for (int i=0; i<channelCount; i++) {
						chunk[i] = samples.get(i).getValue();
					}

					// przesłanie chunka do recordera
					if (sampleQueue != null)
						sampleQueue.offer( chunk.clone());

					// kondycjonowanie chunka z danymi do wyświetlenia
					double[] condChunk = new double[plotCount];
					for (int i=0; i<plotCount; i++) {
						int n = selectedChannels[i];
						condChunk[i] = gain[n] * chunk[n] + offset[n];
//						out.print( "\t");
//						s = Double.toString( condChunk[i]);
//						s = s.replace( '.', ',');
//						out.print( s);
					}
//					out.println();

					publish( condChunk);
				}
				else if (sampleMsg.getType() == SvarogConstants.MessageTypes.TAG) {

					logger.debug( "Tag recorder: got a tag!");

					ByteString msgString = sampleMsg.getMessage();
					SvarogProtocol.Tag tagMsg = null;
					try {
						tagMsg = SvarogProtocol.Tag.parseFrom( msgString);
					} 
					catch (Exception e) {
						e.printStackTrace();
						continue;
					}

					// TODO dodać obsługę stylów - może wybór z kakiejś palety dla poszczególnych nazw i kanałów
					TagStyle style = new TagStyle( SignalSelectionType.CHANNEL, tagMsg.getName(), tagMsg.getName(), Color.RED, Color.BLUE, 2);
					String channels = tagMsg.getChannels();
					StringTokenizer st = new StringTokenizer( channels, " ");
					int n = st.countTokens();
					for (int i=0; i<n; i++) {
						String s = st.nextToken();
						int channel = Integer.parseInt( s);
						Tag tag = new Tag( style, 
								(float) tagMsg.getStartTimestamp(), 
								(float) tagMsg.getEndTimestamp(), 
								channel);
						publish( tag);
					}
				}
				else {
					logger.error( "received bad reply! " + sampleMsg.getMessage());

					logger.debug( "Worker: receive failed!");

					return null;
				}

			}
			catch (InterruptedException e) {
				logger.error("receiveing failed! " + e.getMessage());
				return null;
			}
		}

		logger.debug( "Worker: stopping serwer...");

		builder = MultiplexerMessage.newBuilder();
		builder.setType( SvarogConstants.MessageTypes.SIGNAL_STREAMER_STOP);
		msg = jmxClient.createMessage(builder);

		// send message
		try {
			ChannelFuture sendingOperation = jmxClient.send( msg, SendingMethod.THROUGH_ONE);
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

	@Override
	protected void process( List< Object> objs) {
		for (Object o : objs) {
			if (o instanceof double[]) {
                                try {
                                    sampleSource.getSemaphore().acquire();
                                    sampleSource.addSamples( (double[]) o);
                                } catch (InterruptedException ex) {
                                    java.util.logging.Logger.getLogger(MonitorWorker.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                finally{
                                    sampleSource.getSemaphore().release();
                                }
			}
			else {
				tagSet.addTag( (Tag) o);
				firePropertyChange( "newTag", null, (Tag) o);
			}
		}
	}

	@Override
	protected void done() {
		finished = true;
		firePropertyChange( "tagsRead", null, tagSet);
	}

	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public boolean isFinished() {
		return finished;
	}

}
