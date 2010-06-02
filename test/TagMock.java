import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

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


public class TagMock {

    protected static final Logger logger = Logger.getLogger( TagMock.class);

    protected JmxClient connection;
    protected MockWorker worker;
    protected List<Long> tagMilis = new ArrayList<Long>();

    public class MockWorker extends SwingWorker<Void, Void> {

        public MockWorker() {
            super();
        }

        @Override
        protected Void doInBackground() throws Exception {

            double time = tagMilis.get( 0).doubleValue() / 1000.0;
            Thread.sleep( tagMilis.get( 0).longValue());

            for (int i=1; i<tagMilis.size(); i++) {
                Long milis = tagMilis.get( i);
                Tag.Builder tagBuilder = Tag.newBuilder();
                tagBuilder.setStartTimestamp( time);
                time += milis.doubleValue() / 1000.0;
                tagBuilder.setEndTimestamp( time);
                tagBuilder.setName( "tag" + i);
                tagBuilder.setChannels( "1 2 3"); // TODO dodać losowanie liczby kanałów i kanałów z podanej listy
                VariableVector.Builder variableVectorBuilder = VariableVector.newBuilder();
                Variable.Builder variableBuilder = Variable.newBuilder();
                variableBuilder.setKey( "abc");
                variableBuilder.setValue( "qwerty");
                variableVectorBuilder.addVariables( variableBuilder.build());
                variableBuilder = Variable.newBuilder();
                variableBuilder.setKey( "def");
                variableBuilder.setValue( "zxcvbn");
                variableVectorBuilder.addVariables( variableBuilder.build());
                tagBuilder.setDesc( variableVectorBuilder.build());
                Tag tag = tagBuilder.build();
                MultiplexerMessage mm = connection.createMessage( tag.toByteString(), SvarogConstants.MessageTypes.TAG);
                connection.send( mm, SendingMethod.THROUGH_ALL);
                Thread.sleep( milis.longValue());
            }
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

        TagMock mock = new TagMock();

        for (String arg : args) {
            mock.tagMilis.add( Long.parseLong( arg));
        }

        System.out.print( "Connecting ... ");
        mock.connection = new JmxClient( SvarogConstants.PeerTypes.TAGS_SENDER);
        SocketAddress socketAddress = new InetSocketAddress( "127.0.0.1", 31889);
        mock.connection.connect( socketAddress);
        System.out.println( "Connected!");

        mock.worker = mock.new MockWorker();
        mock.worker.execute();

    }

}
