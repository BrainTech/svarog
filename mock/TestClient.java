

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import multiplexer.jmx.client.ConnectException;
import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.jmx.exceptions.OperationFailedException;
import multiplexer.jmx.server.JmxServer;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol.Sample;
import org.signalml.multiplexer.protocol.SvarogProtocol.SampleVector;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class TestClient {

	/**
	 * Send a message to {@link EchoBackend} via a {@link JmxServer} run by
	 * {@link JmxServerRunner}.
	 * 
	 * @throws NoPeerForTypeException
	 * @throws OperationFailedException
	 * @throws InterruptedException
	 * @throws ConnectException 
	 * @throws InvalidProtocolBufferException 
	 */
	public static void main(String[] args) throws OperationFailedException,
		NoPeerForTypeException, InterruptedException, ConnectException, InvalidProtocolBufferException {

		System.out.println( "Connecting ...");
		JmxClient client = new JmxClient( SvarogConstants.PeerTypes.STREAM_RECEIVER);
		SocketAddress socketAddress = new InetSocketAddress( "127.0.0.1", 31889);
		client.connect( socketAddress);
		System.out.println( "Connected!");

		System.out.println( "Sending ...");
		ByteString message = ByteString.copyFromUtf8("1 2 3");
		MultiplexerMessage mm = client.createMessage( message, SvarogConstants.MessageTypes.SIGNAL_STREAMER_START);
		client.send( mm, SendingMethod.THROUGH_ONE);
		System.out.println( "Sent!");
 
		System.out.println( "Receiving ...");
		IncomingMessageData imsg = client.receive();
		MultiplexerMessage mmsg = imsg.getMessage();
		System.out.println( "Received!");
		int type = mmsg.getType();
		System.out.println( type);
		if (type != SvarogConstants.MessageTypes.STREAMED_SIGNAL_MESSAGE)
			System.out.println( "Bad response!");
		ByteString bstr = mmsg.getMessage();
		System.out.println( "stream size: " + bstr.size());
		SampleVector sv = SampleVector.parseFrom( bstr);
		for (int i=0; i<sv.getSamplesCount(); i++) {
			Sample s = sv.getSamples( i);
			double t = s.getTimestamp();
			double v = s.getValue();
			System.out.println( "sample: " + t + ":" + v);
		}

		System.out.println( "Sending ...");
		mm = client.createMessage( message, SvarogConstants.MessageTypes.SIGNAL_STREAMER_STOP);
		client.send( mm, SendingMethod.THROUGH_ONE);
		System.out.println( "Sent!");

		client.shutdown();
	}

}
