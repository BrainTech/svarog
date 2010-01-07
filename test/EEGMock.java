import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.ConnectException;
import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Constants;
import multiplexer.protocol.Protocol.MultiplexerMessage;
import multiplexer.protocol.Protocol.Sample;
import multiplexer.protocol.Protocol.SampleVector;

import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;


public class EEGMock {

    protected static final Logger logger = Logger.getLogger( EEGMock.class);

    protected JmxClient connection;
    protected MockWorker worker;

    public class MockWorker extends SwingWorker<Void, Void> {

        int channelCount;
        int samplePeriod;

        public MockWorker( int n, int period) {
            super();
            channelCount = n;
            samplePeriod = period;
        }

        @Override
        protected Void doInBackground() throws Exception {

//            System.out.println( " worker start...");
            Thread.sleep( samplePeriod);
            while (!isCancelled()) {
                SampleVector.Builder sampleVectorBuilder = SampleVector.newBuilder();
                for (int i=0; i<channelCount; i++) {
                    Sample.Builder sampleBuilder = Sample.newBuilder();
                    sampleBuilder.setTimestamp( (i * samplePeriod) / 1000.0);
                    sampleBuilder.setValue( Math.random() * 4000.0 - 2000.0);
                    sampleVectorBuilder.addSamples( sampleBuilder);
                }
                MultiplexerMessage mm = connection.createMessage( sampleVectorBuilder.build().toByteString(), Constants.MessageTypes.STREAMED_SIGNAL_MESSAGE);
//                System.out.println( "sending...");
                connection.send( mm, SendingMethod.THROUGH_ONE);
//                System.out.println( "sent!");
                Thread.sleep( samplePeriod);
            }
            return null;
        }

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

        System.out.print( "Connecting ... ");
        mock.connection = new JmxClient( Constants.PeerTypes.SIGNAL_STREAMER);
        mock.connection.connect(JmxServerRunner.jmxServerAddress());
        System.out.println( "Connected!");
 
        while (true) {
            System.out.print( "Receiving ... ");
            IncomingMessageData imsg = mock.connection.receive();
            MultiplexerMessage mmsg = imsg.getMessage();
            System.out.println( "Received!");
            int type = mmsg.getType();
            System.out.println( type);
    
            if (type == Constants.MessageTypes.SIGNAL_STREAMER_START) {
                ByteString bstr = mmsg.getMessage();
                String s = bstr.toStringUtf8();
                StringTokenizer st = new StringTokenizer( s, " ");
                int n = st.countTokens();
                mock.worker = mock.new MockWorker( n, 100);
                mock.worker.execute();
            }
            else if (type == Constants.MessageTypes.SIGNAL_STREAMER_STOP) {
                System.out.println( "stream closed!");
                mock.worker.cancel( false);
//                mock.connection.shutdown();
            }
            else
                System.out.println( "Bad message!");
        }


    }

}
