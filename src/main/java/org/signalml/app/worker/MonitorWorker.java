package org.signalml.app.worker;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
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
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;

import com.google.protobuf.ByteString;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorker< Void, double[]> {

    protected static final Logger logger = Logger.getLogger( MonitorWorker.class);

    public static final int TIMEOUT_MILIS = 50;

	private JmxClient jmxClient;
	private OpenMonitorDescriptor monitorDescriptor;
	private LinkedBlockingQueue< double[]> sampleQueue;
	private RoundBufferSampleSource sampleSource;

	public MonitorWorker( JmxClient jmxClient, OpenMonitorDescriptor monitorDescriptor, RoundBufferSampleSource sampleSource) {
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

        PrintWriter out = new PrintWriter( new File( "recv_data.tsv"));

        while (!isCancelled()) {

            logger.debug( "Worker: receiving!");
            // receive message
            IncomingMessageData msgData = null;
            try {
                msgData = jmxClient.receive( TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
                if (msgData == null)
                    continue;
                MultiplexerMessage sampleMsg = msgData.getMessage();
                int type = sampleMsg.getType();
                logger.debug( "Worker: received message type: " + type);
                if (!(sampleMsg.getType() == SvarogConstants.MessageTypes.STREAMED_SIGNAL_MESSAGE)) {
                    logger.error( "received bad reply! " + sampleMsg.getMessage());

                    logger.debug( "Worker: receive failed!");

                    return null;
                }

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

                String s = Double.toString( samples.get(0).getTimestamp());
                s = s.replace( '.', ',');
                out.print( s);

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
                    out.print( "\t");
                    s = Double.toString( condChunk[i]);
                    s = s.replace( '.', ',');
                    out.print( s);
                }
                out.println();

                publish( condChunk);
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
    protected void process( List<double[]> chunks) {
        sampleSource.addSamples( chunks);
    }

}
