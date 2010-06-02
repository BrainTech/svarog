import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.ConnectException;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol.Tag;
import org.signalml.multiplexer.protocol.SvarogProtocol.Variable;
import org.signalml.multiplexer.protocol.SvarogProtocol.VariableVector;


public class TagSeqMock {

    protected static final Logger logger = Logger.getLogger( TagSeqMock.class);

    protected JmxClient connection;
    protected MockWorker worker;
    protected long milis;

    static String createChannelList() {
        int n = (int) Math.round( (Math.random() * 20));
        Set<Integer> chan = new TreeSet<Integer>();
        while (chan.size()<n)
            chan.add( (int) Math.floor( (Math.random() * 20)) + 1);
        Integer[] ch = new Integer[n];
        chan.toArray( ch);
        Arrays.sort( ch);
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<n-1; i++)
            buf.append( ch[i]).append( " ");
        buf.append( ch[n-1]);
        return buf.toString();
    }
    
    static String randomString() {
        int n = (int) Math.round( (Math.random() * 16));
        String str = new String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int te = 0;
        for (int i=1; i<=n; i++){
            te = r.nextInt(62);
            sb.append(str.charAt(te));
        }
        return sb.toString();
    }

    
    public class MockWorker extends SwingWorker<Void, Void> {

        public MockWorker() {
            super();
        }

        @Override
        protected Void doInBackground() throws Exception {

            double time = (double) milis / 1000.0;
            Thread.sleep( (long) time);

            for ( int i=0; true; i++) {
                logger.info( "iter: " + i);
                Tag.Builder tagBuilder = Tag.newBuilder();
                tagBuilder.setStartTimestamp( time);
                logger.info( "start time: " + time);
                time += (double) milis / 1000.0;
                tagBuilder.setEndTimestamp( time);
                logger.info( "end time: " + time);
                tagBuilder.setName( "tag" + i);
                tagBuilder.setChannels( createChannelList());
                VariableVector.Builder variableVectorBuilder = VariableVector.newBuilder();
                Variable.Builder variableBuilder = Variable.newBuilder();
                variableBuilder.setKey( "abc");
                variableBuilder.setValue( randomString());
                variableVectorBuilder.addVariables( variableBuilder.build());
                variableBuilder = Variable.newBuilder();
                variableBuilder.setKey( "def");
                variableBuilder.setValue( randomString());
                variableVectorBuilder.addVariables( variableBuilder.build());
                tagBuilder.setDesc( variableVectorBuilder.build());
                Tag tag = tagBuilder.build();
                MultiplexerMessage mm = connection.createMessage( tag.toByteString(), SvarogConstants.MessageTypes.TAG);
                connection.send( mm, SendingMethod.THROUGH_ALL);
                Thread.sleep( milis);
            }

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

        TagSeqMock mock = new TagSeqMock();

        mock.milis = Long.parseLong( args[0]);

        logger.info( "Connecting ... ");
        mock.connection = new JmxClient( SvarogConstants.PeerTypes.TAGS_SENDER);
        SocketAddress socketAddress = new InetSocketAddress( "127.0.0.1", 31889);
        mock.connection.connect( socketAddress);
        logger.info( "Connected!");

        mock.worker = mock.new MockWorker();
        mock.worker.execute();

    }

}
