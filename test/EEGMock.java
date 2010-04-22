import java.io.File;
import java.io.FileOutputStream;
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

//        int samplePeriod;

        public MockWorker() {
            super();
//            samplePeriod = period;
        }

        @Override
        protected Void doInBackground() throws Exception {

            float time = 0f;
            PrintWriter out = new PrintWriter( new File( "gen_data.tsv"));

//            System.out.println( " worker start...");
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
//                System.out.println( "sending...");
                connection.send( mm, SendingMethod.THROUGH_ONE);
//                System.out.println( "sent!");
                Thread.sleep( PERIOD_MILIS, PERIOD_NANOS);
            }
            out.close();
            return null;
        }

    }

//    public class ParamsWorker extends SwingWorker<Void, Void> {
//
//        int channelCount;
//        int samplePeriod;
//
//        public ParamsWorker( int n, int period) {
//            super();
//            channelCount = n;
//            samplePeriod = period;
//        }
//
//        @Override
//        protected Void doInBackground() throws Exception {
//
////            System.out.println( " worker start...");
//            Thread.sleep( samplePeriod);
//            while (!isCancelled()) {
//                SampleVector.Builder sampleVectorBuilder = SampleVector.newBuilder();
//                for (int i=0; i<channelCount; i++) {
//                    Sample.Builder sampleBuilder = Sample.newBuilder();
//                    sampleBuilder.setTimestamp( (i * samplePeriod) / 1000.0);
//                    sampleBuilder.setValue( Math.random() * 4000.0 - 2000.0);
//                    sampleVectorBuilder.addSamples( sampleBuilder);
//                }
//                MultiplexerMessage mm = connection.createMessage( sampleVectorBuilder.build().toByteString(), SvarogConstants.MessageTypes.STREAMED_SIGNAL_MESSAGE);
////                System.out.println( "sending...");
//                connection.send( mm, SendingMethod.THROUGH_ONE);
////                System.out.println( "sent!");
//                Thread.sleep( samplePeriod);
//            }
//            return null;
//        }
//
//    }

    @Override
    protected void finalize() throws Throwable {
        connection.shutdown();
        super.finalize();
    }

//    public class MockRequestHandler implements MessageHandler {
//
//        public void handleMessage( MultiplexerMessage message, MessageContext ctx) {
//            if (message.getType() == Constants.MessageTypes.SIGNAL_STREAMER_START) {
//                ByteString bstr = message.getMessage();
//                String s = bstr.toStringUtf8();
//                StringTokenizer st = new StringTokenizer( s, " ");
//                
//                int n = st.countTokens();
//                if (n != 3)
//                    System.out.println( "Bad channel count");
//                SampleVector.Builder sampleVectorBuilder = SampleVector.newBuilder();
//                Sample.Builder sampleBuilder = Sample.newBuilder();
//                sampleBuilder.setTimestamp( 1.0);
//                sampleBuilder.setValue(100.0);
//                sampleVectorBuilder.addSamples( sampleBuilder);
//                sampleBuilder = Sample.newBuilder();
//                sampleBuilder.setTimestamp( 1.0);
//                sampleBuilder.setValue(200.0);
//                sampleVectorBuilder.addSamples( sampleBuilder);
//                sampleBuilder = Sample.newBuilder();
//                sampleBuilder.setTimestamp( 1.0);
//                sampleBuilder.setValue(300.0);
//                sampleVectorBuilder.addSamples( sampleBuilder);
//                MultiplexerMessage mm = connection.createMessage( sampleVectorBuilder.build().toByteString(), Constants.MessageTypes.STREAMED_SIGNAL_MESSAGE);
////                System.out.println( "sending first ...");
//                try {
//                    connection.send( mm, SendingMethod.THROUGH_ONE);
//                }
//                catch (NoPeerForTypeException e) {
//                    e.printStackTrace();
//                }
////                System.out.println( "first sent!");
//                worker = new MockWorker( n, 100);
//                worker.execute();
//            }
//            else if (message.getType() == Constants.MessageTypes.SIGNAL_STREAMER_STOP) {
//                System.out.println( "strem closed!");
//                worker.cancel( false);
//            }
//            else
//                System.out.println( "Bad message!");
//        }
//    }



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
//                ByteString bstr = mmsg.getMessage();
//                String s = bstr.toStringUtf8();
//                StringTokenizer st = new StringTokenizer( s, " ");
//                int n = st.countTokens();
                mock.worker = mock.new MockWorker();
                mock.worker.execute();
            }
            else if (type == SvarogConstants.MessageTypes.SIGNAL_STREAMER_STOP) {
                System.out.println( "stream closed!");
                mock.worker.cancel( false);
//                mock.connection.shutdown();
            }
            else
                System.out.println( "Bad message!");
        }

    }

}
