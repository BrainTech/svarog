

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import multiplexer.jmx.client.ConnectException;
import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.jmx.exceptions.OperationFailedException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol.Tag;
import org.signalml.multiplexer.protocol.SvarogProtocol.Variable;
import org.signalml.multiplexer.protocol.SvarogProtocol.VariableVector;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class TestTagClient {

	JmxClient client;
	public static void main(String[] args) throws OperationFailedException,
		NoPeerForTypeException, InterruptedException, ConnectException, InvalidProtocolBufferException {

		System.out.println( "Connecting ...");
		JmxClient client = new JmxClient(SvarogConstants.PeerTypes.STREAM_RECEIVER);
		SocketAddress socketAddress = new InetSocketAddress( "127.0.0.1", 31889);
		client.connect(socketAddress);
		System.out.println( "Connected!");

		while (true) {
			System.out.println( "Receiving ...");
			IncomingMessageData imsg = client.receive();
			MultiplexerMessage mmsg = imsg.getMessage();
			System.out.println( "Received!");
			int type = mmsg.getType();
			System.out.println(type);
			if (type != SvarogConstants.MessageTypes.TAG)
				System.out.println( "Bad response!");
			ByteString bstr = mmsg.getMessage();
			System.out.println( "stream size: " + bstr.size());
			Tag tag = Tag.parseFrom(bstr);
			System.out.println( "name: " + tag.getName());
			System.out.println( "channels: " + tag.getChannels());
			System.out.println( "startTimestamp: " + tag.getStartTimestamp());
			System.out.println( "EndTimestamp: " + tag.getEndTimestamp());
			VariableVector desc = tag.getDesc();
			System.out.println( "desc size: " + desc.getVariablesCount());
			for (Variable var : desc.getVariablesList()) {
				System.out.println( "===============");
				System.out.println( "key: " + var.getKey());
				System.out.println( "value: " + var.getValue());
			}
		}

	}

	@Override
	protected void finalize() throws Throwable {
		client.shutdown();
		super.finalize();
	}

}
