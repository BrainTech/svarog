import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.ConnectException;
import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;


public class EEGMock {

	protected static final Logger logger = Logger.getLogger( EEGMock.class);
	
	public static final int CHANNEL_COUNT = 20;
	public static final double SAMPLE_PERIOD = 90.90909;
	public static final double TIME_DELTA = ((float) SAMPLE_PERIOD) / 1000f;
	public static final long PERIOD_MILIS = 90;
	public static final int PERIOD_NANOS = 909090;

	protected JmxClient connection;
	protected MockWorker worker;
	protected double gain = 2.0;
	protected double offset = 1.0;
	
	public class MockWorker extends SwingWorker<Void, Void> {

		public MockWorker() {
			super();
		}

		@Override
		protected Void doInBackground() throws Exception {

			float time = 0f;
			PrintWriter out = new PrintWriter( new File( "gen_data.tsv"));

			Thread.sleep( PERIOD_MILIS, PERIOD_NANOS);
			while (!isCancelled()) {
				time += TIME_DELTA;
				out.print( time);
				SampleVector.Builder sampleVectorBuilder = SampleVector.newBuilder();
				for (int i=0; i<CHANNEL_COUNT; i++) {
					Sample.Builder sampleBuilder = Sample.newBuilder();
					sampleBuilder.setTimestamp( time);
					double val = Math.random() * gain - offset;
					sampleBuilder.setValue( val);
					sampleVectorBuilder.addSamples( sampleBuilder);
					out.print( "\t");
					String s = Double.toString( val);
					s = s.replace( '.', ',');
					out.print( s);
				}
				out.println();
				MultiplexerMessage mm = connection.createMessage( sampleVectorBuilder.build().toByteString(), SvarogConstants.MessageTypes.STREAMED_SIGNAL_MESSAGE);
				connection.send( mm, SendingMethod.THROUGH_ONE);
				Thread.sleep( PERIOD_MILIS, PERIOD_NANOS);
			}
			out.close();
			return null;
		}

	}

	@Override
	protected void finalize() throws Throwable {
		connection.shutdown();
		super.finalize();
	}

	/**
	 * @param args
	 * @throws ConnectException 
	 * @throws NoPeerForTypeException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ConnectException, NoPeerForTypeException, InterruptedException {

		EEGMock mock = new EEGMock();

		if (args.length > 0)
			mock.gain = Double.parseDouble( args[0]);
		if (args.length > 1)
			mock.offset = Double.parseDouble( args[1]);

		System.out.print( "Connecting ... ");
		mock.connection = new JmxClient( SvarogConstants.PeerTypes.SIGNAL_STREAMER);
		SocketAddress socketAddress = new InetSocketAddress( "127.0.0.1", 31889);
		mock.connection.connect( socketAddress);
		System.out.println( "Connected!");
 
		while (true) {
			System.out.print( "Receiving ... ");
			IncomingMessageData imsg = mock.connection.receive();
			MultiplexerMessage mmsg = imsg.getMessage();
			System.out.println( "Received!");
			int type = mmsg.getType();
			System.out.println( type);
	
			if (type == SvarogConstants.MessageTypes.SIGNAL_STREAMER_START) {
				mock.worker = mock.new MockWorker();
				mock.worker.execute();
			}
			else if (type == SvarogConstants.MessageTypes.SIGNAL_STREAMER_STOP) {
				System.out.println( "stream closed!");
				mock.worker.cancel( false);
			}
			else
				System.out.println( "Bad message!");
		}

	}

}
