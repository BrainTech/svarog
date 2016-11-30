package org.signalml.app.logging;

import com.getsentry.raven.connection.Connection;
import com.getsentry.raven.connection.ConnectionException;
import com.getsentry.raven.connection.EventSendFailureCallback;
import com.getsentry.raven.event.Event;
import com.getsentry.raven.marshaller.Marshaller;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * OBCI connection implementation for Raven.
 * Sends data as ZMQ messages in JSON format to given OBCI REP socket.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ObciConnection extends ZmqRemoteSender implements Connection {

	private Marshaller marshaller;

	// dateformat definition compatible with Raven's JsonMarshaller
	private static final DateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	static { ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC")); }

	/**
	 * Create a new connection to given REP socket.
	 * Internal thread will be created and started immediately.
	 *
	 * @param obciRepUrl  address (e.g. "tcp://host:123") of the remote REP socket
	 */
	public ObciConnection(String obciRepUrl) {
		super(obciRepUrl);
		startThread();
	}

	@Override
	public void addEventSendFailureCallback(EventSendFailureCallback eventSendFailureCallback) {
		// not supported
	}

	@Override
	public void send(Event event) throws ConnectionException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		marshaller.marshall(event, outputStream);
		String dataString = outputStream.toString();

		try {
			JSONObject data = new JSONObject(dataString);
			if (data.has("timestamp")) {
				// OBCI expects timestamp as float
				String timestampString = data.getString("timestamp");
				long timeMillis;
				try {
					timeMillis = ISO_FORMAT.parse(timestampString).getTime();
				} catch (ParseException ex) {
					// falling back to current timestamp
					timeMillis = System.currentTimeMillis();
				}
				data.put("timestamp", 0.001 * timeMillis);
			}
			String message = (new JSONObject())
				.put("type", "sentry")
				.put("log_type", "sentry")
				.put("data", data)
				.toString();
			offer(message);
		} catch (JSONException json) {
			// not really a possibility
		}
	}

	/**
	 * Set a marshaller object to be used in order to convert message
	 * into JSON format to be sent. The given implementation must NOT
	 * use compression.
	 *
	 * @param marshaller  Marshaller implementation
	 */
	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

}
