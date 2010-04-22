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

    public static final String SAMPLING_RATE      = "SamplingRate";
    public static final String NUMBER_OF_CHANNELS = "NumOfChannels";
    public static final String AMPLIFIER_CHANNELS = "AmplifierChannelsToRecord";
    public static final String CHANNEL_NAMES      = "ChannelsNames";
    public static final String CALIBRATION_GAIN   = "Gain";
    public static final String CALIBRATION_OFFSET = "Offset";
    public static final String MINIMUN_VALUE      = "MinData";
    public static final String MAXIMUN_VALUE      = "MaxData";

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

	@Override
	protected OpenMonitorDescriptor doInBackground() throws Exception {

	    int step = 0;
	    int channelCount = -1;
	    
	    logger.info("Gathering metadata...");

        logger.info("Sending sampling request...");

        // create message
        MultiplexerMessage.Builder builder = MultiplexerMessage.newBuilder();
        ByteString msgBody = ByteString.copyFromUtf8( SAMPLING_RATE);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        MultiplexerMessage msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending sampling request failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.samplingRate.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.samplingRate.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.samplingRate.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving sampling rate...");

        // receive message
        IncomingMessageData msgData = null;
        try {
            msgData = client.receive( timeout);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.samplingRate.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
//                    SvarogProtocol.Variable variable = SvarogProtocol.Variable.parseFrom( bs);
                    String val = bs.toStringUtf8();
                    Float freq = new Float( val);
                    openMonitorDescriptor.setSamplingFrequency( freq);
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.samplingRate.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        } 
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.samplingRate.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

// channel count

        logger.info("Sending channel count request...");

        // create message
        builder = MultiplexerMessage.newBuilder();
        msgBody = ByteString.copyFromUtf8( NUMBER_OF_CHANNELS);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending channel count failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.channelCount.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.channelCount.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.channelCount.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving channel count rate...");

        // receive message
        msgData = null;
        try {
            msgData = client.receive( 10000);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.channelCount.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
                    String val = bs.toStringUtf8();
                    channelCount = Integer.parseInt( val);
                    openMonitorDescriptor.setChannelCount( new Integer(channelCount));
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.channelCount.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        } 
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.channelCount.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

// amplifier channels

        logger.info("Sending AmplifierChannelsToRecord request...");

        // create message
        builder = MultiplexerMessage.newBuilder();
        msgBody = ByteString.copyFromUtf8( AMPLIFIER_CHANNELS);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending amplifier channels failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.amplifierChannels.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.amplifierChannels.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.amplifierChannels.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving amplifier channels rate...");

        // receive message
        msgData = null;
        try {
            msgData = client.receive( 10000);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.amplifierChannels.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
                    String val = bs.toStringUtf8();
                    StringTokenizer st = new StringTokenizer( val, " ");
                    int[] amplifierChannels = new int[channelCount];
                    for (int i=0; i<channelCount && st.hasMoreTokens(); i++) {
                        String s = st.nextToken();
                        amplifierChannels[i] = Integer.parseInt( s);
                    }
                    openMonitorDescriptor.setAmplifierChannels( amplifierChannels);
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.channelCount.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.channelCount.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

 // channel labels

         logger.info("Sending channel names request...");

         // create message
         builder = MultiplexerMessage.newBuilder();
         msgBody = ByteString.copyFromUtf8( CHANNEL_NAMES);
         builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
         msg = client.createMessage( builder);

         // send message
         try {
             ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
             sendingOperation.await(1, TimeUnit.SECONDS);
             if (!sendingOperation.isSuccess()) {
                 logger.info("sending channel names failed!");
                 String info = messageSource.getMessage( 
                         "action.openMonitor.metadataWorker.channelNames.sendingFailedMsg");
                 openMonitorDescriptor.setMetadataInfo( info);
                 return openMonitorDescriptor;
             }
         }
         catch (NoPeerForTypeException e) {
             logger.error("sending failed! " + e.getMessage());
             String info = messageSource.getMessage( 
                     "action.openMonitor.metadataWorker.channelNames.sendingFailedMsg");
             openMonitorDescriptor.setMetadataInfo( info);
             return openMonitorDescriptor;
         }
         catch (InterruptedException e) {
             logger.error("sending failed! " + e.getMessage());
             String info = messageSource.getMessage( 
                     "action.openMonitor.metadataWorker.channelNames.sendingFailedMsg");
             openMonitorDescriptor.setMetadataInfo( info);
             return openMonitorDescriptor;
         }

         logger.debug( "Receiving channel names gain...");

         // receive message
         msgData = null;
         try {
             msgData = client.receive( timeout);
             if (msgData != null) {
                 MultiplexerMessage reply = msgData.getMessage();
                 if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                     logger.error("received bad reply! " + reply.getMessage());
                     String info = messageSource.getMessage( 
                             "action.openMonitor.metadataWorker.channelNames.receivedBadReplyMsg");
                     openMonitorDescriptor.setMetadataInfo( info);
                     return openMonitorDescriptor;
                 }
                 else {
                     ByteString bs = reply.getMessage();
                     String val = bs.toStringUtf8();
                     StringTokenizer st = new StringTokenizer( val, ";");
                     String[] channelLabels = new String[channelCount];
                     for (int i=0; i<channelCount && st.hasMoreTokens(); i++)
                         channelLabels[i] = st.nextToken();
                     openMonitorDescriptor.setChannelLabels( channelLabels);
                 }
             }
             else {
                 logger.info("receive timed out!");
                 String info = messageSource.getMessage( 
                         "action.openMonitor.metadataWorker.channelNames.receiveTimedout");
                 openMonitorDescriptor.setMetadataInfo( info);
                 return openMonitorDescriptor;
             }
         } 
         catch (InterruptedException e) {
             logger.error("receiveing failed! " + e.getMessage());
             String info = messageSource.getMessage( 
                     "action.openMonitor.metadataWorker.channelNames.receivingFailedMsg");
             openMonitorDescriptor.setMetadataInfo( info);
             return openMonitorDescriptor;
         }

         publish( ++step);

// calibration gain

        logger.info("Sending calibration gain request...");

        // create message
        builder = MultiplexerMessage.newBuilder();
        msgBody = ByteString.copyFromUtf8( CALIBRATION_GAIN);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending calibration gain failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.calibrationGain.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.calibrationGain.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.calibrationGain.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving calibration gain...");

        // receive message
        msgData = null;
        try {
            msgData = client.receive( timeout);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.calibrationGain.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
                    String val = bs.toStringUtf8();
                    StringTokenizer st = new StringTokenizer( val, " ");
                    float[] gain = new float[channelCount];
                    for (int i=0; i<channelCount && st.hasMoreTokens(); i++) {
                        String s = st.nextToken();
                        gain[i] = Float.parseFloat( s);
                    }
                    openMonitorDescriptor.setCalibrationGain( gain);
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.calibrationGain.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        } 
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.calibrationGain.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

// calibration offset

        logger.info("Sending calibration offset request...");

        // create message
        builder = MultiplexerMessage.newBuilder();
        msgBody = ByteString.copyFromUtf8( CALIBRATION_OFFSET);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending calibration offset failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.calibrationOffset.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.calibrationOffset.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.calibrationOffset.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving calibration offset...");

        // receive message
        msgData = null;
        try {
            msgData = client.receive( timeout);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.calibrationOffset.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
                    String val = bs.toStringUtf8();
                    StringTokenizer st = new StringTokenizer( val, " ");
                    float[] offset = new float[channelCount];
                    for (int i=0; i<channelCount && st.hasMoreTokens(); i++) {
                        String s = st.nextToken();
                        offset[i] = Float.parseFloat( s);
                    }
                    openMonitorDescriptor.setCalibrationOffset( offset);
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.calibrationOffset.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        } 
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.calibrationOffset.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

// minimum value

        logger.info("Sending minimum value request...");

        // create message
        builder = MultiplexerMessage.newBuilder();
        msgBody = ByteString.copyFromUtf8( MINIMUN_VALUE);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending minimum value failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.minimumValue.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.minimumValue.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.minimumValue.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving minimum value...");

        // receive message
        msgData = null;
        try {
            msgData = client.receive( timeout);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.minimumValue.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
                    String val = bs.toStringUtf8();
                    float minVal = Float.parseFloat( val);
                    openMonitorDescriptor.setMinimumValue( minVal);
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.minimumValue.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        } 
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.minimumValue.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

// maximum value

        logger.info("Sending maximum value request...");

        // create message
        builder = MultiplexerMessage.newBuilder();
        msgBody = ByteString.copyFromUtf8( MAXIMUN_VALUE);
        builder.setType( SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE).setMessage( msgBody);
        msg = client.createMessage( builder);

        // send message
        try {
            ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
            sendingOperation.await(1, TimeUnit.SECONDS);
            if (!sendingOperation.isSuccess()) {
                logger.info("sending maximum value failed!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.maximumValue.sendingFailedMsg");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        }
        catch (NoPeerForTypeException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.maximumValue.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }
        catch (InterruptedException e) {
            logger.error("sending failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.maximumValue.sendingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }


        logger.debug( "Receiving maximum value...");

        // receive message
        msgData = null;
        try {
            msgData = client.receive( timeout);
            if (msgData != null) {
                MultiplexerMessage reply = msgData.getMessage();
                if (reply.getType() != SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE) {
                    logger.error("received bad reply! " + reply.getMessage());
                    String info = messageSource.getMessage( 
                            "action.openMonitor.metadataWorker.maximumValue.receivedBadReplyMsg");
                    openMonitorDescriptor.setMetadataInfo( info);
                    return openMonitorDescriptor;
                }
                else {
                    ByteString bs = reply.getMessage();
                    String val = bs.toStringUtf8();
                    float maxVal = Float.parseFloat( val);
                    openMonitorDescriptor.setMaximumValue( maxVal);
                }
            }
            else {
                logger.info("receive timed out!");
                String info = messageSource.getMessage( 
                        "action.openMonitor.metadataWorker.maximumValue.receiveTimedout");
                openMonitorDescriptor.setMetadataInfo( info);
                return openMonitorDescriptor;
            }
        } 
        catch (InterruptedException e) {
            logger.error("receiveing failed! " + e.getMessage());
            String info = messageSource.getMessage( 
                    "action.openMonitor.metadataWorker.maximumValue.receivingFailedMsg");
            openMonitorDescriptor.setMetadataInfo( info);
            return openMonitorDescriptor;
        }

        publish( ++step);

        String info = messageSource.getMessage( 
                "action.openMonitor.metadataWorker.receivedMetadata");
        openMonitorDescriptor.setMetadataReceived( true);
        openMonitorDescriptor.setMetadataInfo( info);
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
