package org.signalml.peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Connects to broker with simplified TCP/IP communication to obtain socket
 * addresses (BrokerInfo instance). Communication is simplified as follows:
 * first four "magic" bytes are sent to broker, and then the broker responds
 * with JSON containing PUB and REP URLs... and that's it.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class BrokerTcpConnector {

	private static final Logger logger = Logger.getLogger(BrokerTcpConnector.class);

	/**
	 * The same magic sequence of four bytes as defined in OBCI.
	 */
	public static final byte[] TCP_MAGIC_BYTES = "OBCI".getBytes();

	/**
	 * Socket connection timeout (in milliseconds).
	 */
	public static final int TIMEOUT_MILIS = 5000;

	/**
	 * Broker TCP/IP address.
	 */
	private final InetSocketAddress brokerTcpAddr;

	/**
	 * Create new instance for connecting with given host and port number.
	 *
	 * @param brokerTcpHost  broker hostname or IP address in string form
	 * @param brokerTcpPort  broker TCP/IP port number, cannot be 0
	 */
	public BrokerTcpConnector(String brokerTcpHost, int brokerTcpPort) {
		brokerTcpAddr = new InetSocketAddress(brokerTcpHost, brokerTcpPort);
	}

	/**
	 * Read all available data from socket, until it closes.
	 *
	 * @param socket  socket object to read data from
	 * @return byte buffer with all data read
	 * @throws IOException  if socket operation fails
	 */
	private static byte[] drainSocket(Socket socket) throws IOException {
		int b;
		ArrayList<Byte> buffer = new ArrayList<>();
		while ((b = socket.getInputStream().read()) >= 0) {
			buffer.add((byte) b);
		}
		byte[] data = new byte[buffer.size()];
		for (int j=0; j<buffer.size(); ++j) {
			data[j] = buffer.get(j);
		}
		return data;
	}

	/**
	 * Convert JSON array object to ordinary array of String instances.
	 *
	 * @param json  JSONArray object
	 * @return array of String instances
	 * @throws JSONException  if given array's elements cannot be converted to string
	 */
	public String[] convertJsonArray(JSONArray json) throws JSONException {
		String[] array = new String[json.length()];
		for (int i=0; i<array.length; ++i) {
			array[i] = json.getString(i);
		}
		return array;
	}

	/**
	 * Connect to broker and fetch broker REP address as well as
	 * PUB and REP addresses for peer, in form of BrokerInfo instance.
	 *
	 * @return PDO with broker data
	 * @throws CommunicationException  if operationg fails due to I/O
	 * or JSON parsing error
	 */
	public BrokerInfo fetchBrokerInfo() throws CommunicationException {
		try (Socket socket = new Socket()) {
			socket.connect(brokerTcpAddr, TIMEOUT_MILIS);

			socket.getOutputStream().write(TCP_MAGIC_BYTES);
			String response = new String(drainSocket(socket));

			JSONObject json = new JSONObject(response);
			String brokerURL = json.getString("broker_url");
			String[] pubURLs = convertJsonArray(json.getJSONArray("pub_urls"));
			String[] repURLs = convertJsonArray(json.getJSONArray("rep_urls"));

			BrokerInfo brokerInfo = new BrokerInfo(brokerURL, pubURLs, repURLs);
			return brokerInfo;
		} catch (IOException | JSONException ex) {
			throw new CommunicationException("failed to fetch broker addresses", ex);
		}
	}

}
