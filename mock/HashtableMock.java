
import java.io.IOException;
import java.net.InetSocketAddress;

import multiplexer.jmx.backend.MessageContext;
import multiplexer.jmx.backend.MessageHandler;
import multiplexer.jmx.backend.SimpleBackend;
import multiplexer.jmx.client.ConnectException;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.signalml.multiplexer.protocol.SvarogConstants;

import com.google.protobuf.ByteString;


public class HashtableMock {

	protected static final Logger logger = Logger.getLogger( HashtableMock.class);

	public static final String MULTIPLEXER_HOST = "127.0.0.1";
	public static final int	MULTIPLEXER_PORT = 31889;

	public static InetSocketAddress jmxServerAddress() {
		return new InetSocketAddress( MULTIPLEXER_HOST, MULTIPLEXER_PORT);
	}

	public static final String SAMPLING_RATE	  = "SamplingRate";
	public static final String NUMBER_OF_CHANNELS = "NumOfChannels";
	public static final String AMPLIFIER_CHANNELS = "AmplifierChannelsToRecord";
	public static final String CHANNEL_NAMES	  = "ChannelsNames";
	public static final String CALIBRATION_GAIN   = "Gain";
	public static final String CALIBRATION_OFFSET = "Offset";
	public static final String MINIMUN_VALUE	  = "MinData";
	public static final String MAXIMUN_VALUE	  = "MaxData";

	public static double gain = 250.0;
	public static double offset = 50.0;
	public static double min = -200.0;
	public static double max = 300.0;

	private static MultiplexerMessage createResponse( MessageContext context, String val) {
		MultiplexerMessage.Builder responseBuilder = null;
		responseBuilder = context.createResponse( 
				SvarogConstants.MessageTypes.DICT_GET_RESPONSE_MESSAGE, 
				ByteString.copyFromUtf8( val));
		return responseBuilder.build();
	}


	private static MultiplexerMessage createSamplingRateResponse( MessageContext context) {
		return createResponse( context, "11");
	}

	private static MultiplexerMessage createNumberOfChanelsResponse( MessageContext context) {
		return createResponse( context, "20");
	}

	private static MultiplexerMessage createAmplifierChannelsToRecordResponse( MessageContext context) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<20; i++) {
			if (i > 0)
				buf.append( " ");
			buf.append( Integer.toString(i));
		}
		String ch = buf.toString();
		return createResponse( context, ch);
	}

	private static MultiplexerMessage createChannelNamesResponse( MessageContext context) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<20; i++) {
			if (i > 0)
				buf.append( ";");
			buf.append( "ch").append( Integer.toString(i));
		}
		String ch = buf.toString();
		return createResponse( context, ch);
	}

	private static MultiplexerMessage createGainResponse( MessageContext context) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<20; i++) {
			if (i > 0)
				buf.append( " ");
//			buf.append( Float.toString( i + 1.5f));
			buf.append( Float.toString( (float) gain));
		}
		String ch = buf.toString();
		return createResponse( context, ch);
	}

	private static MultiplexerMessage createOffsetResponse( MessageContext context) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<20; i++) {
			if (i > 0)
				buf.append( " ");
//			buf.append( Float.toString( i + 2.3f));
			buf.append( Float.toString( (float) offset));
		}
		String ch = buf.toString();
		return createResponse( context, ch);
	}

	private static MultiplexerMessage createMinDataResponse( MessageContext context) {
		return createResponse( context, Float.toString( (float) min));
	}

	private static MultiplexerMessage createMaxDataResponse( MessageContext context) {
		return createResponse( context, Float.toString( (float) max));
	}

	private static class RequestHandler implements MessageHandler {

		public void handleMessage( MultiplexerMessage message, MessageContext context) {
			if (message.getType() == SvarogConstants.MessageTypes.DICT_GET_REQUEST_MESSAGE) {
				MultiplexerMessage response = null;
				String s = message.getMessage().toStringUtf8();
				System.out.println( s);
				if (SAMPLING_RATE.equals(s)) {
					response = createSamplingRateResponse( context);
				}
				else if (NUMBER_OF_CHANNELS.equals(s)) {
					response = createNumberOfChanelsResponse( context);
				}
				else if (AMPLIFIER_CHANNELS.equals(s)) {
					response = createAmplifierChannelsToRecordResponse( context);
				}
				else if (CHANNEL_NAMES.equals(s)) {
					response = createChannelNamesResponse( context);
				}
				else if (CALIBRATION_GAIN.equals(s)) {
					response = createGainResponse( context);
				}
				else if (CALIBRATION_OFFSET.equals(s)) {
					response = createOffsetResponse( context);
				}
				else if (MINIMUN_VALUE.equals(s)) {
					response = createMinDataResponse( context);
				}
				else if (MAXIMUN_VALUE.equals(s)) {
					response = createMaxDataResponse( context);
				}
				else {
					System.out.println( "Bad message!");
					return;
				}
				context.reply( response);
			}
			else
				System.out.println( "Bad request!");
		}
	}

	/**
	 * @param args
	 * @throws ConnectException 
	 * @throws NoPeerForTypeException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ConnectException, NoPeerForTypeException, InterruptedException {
		if (args.length > 0)
			gain = Double.parseDouble( args[0]);
		if (args.length > 1)
			offset = Double.parseDouble( args[1]);
		if (args.length > 2)
			min = Double.parseDouble( args[2]);
		if (args.length > 3)
			max = Double.parseDouble( args[3]);
		SimpleBackend backend = new SimpleBackend( 
				SvarogConstants.PeerTypes.HASHTABLE, 
				new RequestHandler());
		backend.asyncConnect(jmxServerAddress());
		backend.run();
	}

}
