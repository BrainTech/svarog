package org.signalml.app.worker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Constants;
import multiplexer.protocol.Protocol.MultiplexerMessage;
import multiplexer.protocol.Protocol.Sample;
import multiplexer.protocol.Protocol.SampleVector;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.domain.signal.RoundBufferSampleSource;

import com.google.protobuf.ByteString;

/** MonitorWorker
 *
 */
public class MonitorWorker extends SwingWorker< Void, double[]> {

    protected static final Logger logger = Logger.getLogger( MonitorWorker.class);
    
    public static final int TIMEOUT_MILIS = 50;

	private JmxClient jmxClient;
	private RoundBufferSampleSource sampleSource;
	
	public MonitorWorker( JmxClient jmxClient, RoundBufferSampleSource sampleSource) {
	    this.jmxClient = jmxClient;
		this.sampleSource = sampleSource;
	}

	@Override
	protected Void doInBackground() throws Exception {

	    System.out.println( "Worker: start...");


        MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
        builder.setType( Constants.MessageTypes.SIGNAL_STREAMER_START);
        ByteString msgBody = ByteString.copyFromUtf8("1 2 3 4 5");

        builder.setMessage( msgBody);
        MultiplexerMessage msg = jmxClient.createMessage(builder);

        // send message
        try {
            ChannelFuture sendingOperation = jmxClient.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.debug("sending failed!");
                return null;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.debug("sending failed! " + e.getMessage());
            return null;
        }
        catch (InterruptedException e) {
            logger.debug("sending failed! " + e.getMessage());
            return null;
        }

        System.out.println( "Worker: sent streaming request!");

        while (!isCancelled()) {

            System.out.println( "Worker: receiving!");
            // receive message
            IncomingMessageData msgData = null;
            try {
                msgData = jmxClient.receive( TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
                if (msgData == null)
                    continue;
                MultiplexerMessage sampleMsg = msgData.getMessage();
                int type = sampleMsg.getType();
                System.out.println( "Worker: received message type: " + type);
                if (!(sampleMsg.getType() == Constants.MessageTypes.STREAMED_SIGNAL_MESSAGE)) {
                    logger.debug( "received bad reply! " + sampleMsg.getMessage());

                    System.out.println( "Worker: receive failed!");

                    return null;
                }

                System.out.println( "Worker: reading chunk!");

                ByteString msgString = sampleMsg.getMessage();
                SampleVector sampleVector = null;
                try {
                    sampleVector = SampleVector.parseFrom( msgString);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
                List<Sample> samples = sampleVector.getSamplesList();

                double[] chunk = new double[sampleSource.getChannelCount()];
                for (int i=0; i<sampleSource.getChannelCount(); i++) {
                    chunk[i] = samples.get(i).getValue();
                }
                publish( chunk);
            }
            catch (InterruptedException e) {
                logger.debug("receiveing failed! " + e.getMessage());
                return null;
            }
        }

        System.out.println( "Worker: stopping serwer...");

        builder = MultiplexerMessage.newBuilder();
        builder.setType( Constants.MessageTypes.SIGNAL_STREAMER_STOP);
        msg = jmxClient.createMessage(builder);

        // send message
        try {
            ChannelFuture sendingOperation = jmxClient.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.debug("sending failed!");
                return null;
            }
        } 
        catch (NoPeerForTypeException e) {
            logger.debug("sending failed! " + e.getMessage());
            return null;
        } 
        catch (InterruptedException e) {
            logger.debug("sending failed! " + e.getMessage());
            return null;
        }

		return null;
	}

    @Override
    protected void process( List<double[]> chunks) {
        sampleSource.addSamples( chunks);
    }

}
