package org.signalml.app.logging;

import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Log appender implementation sending logs to given REP (ØMQ) socket.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ZmqRemoteAppender extends org.apache.log4j.AppenderSkeleton {

	/** message type for logging, as defined in OBCI source code */
	private static final String MESSAGE_TYPE = "log_msg";

	private final String source;
	private final ZmqRemoteSender sender;

	/**
	 * Create a new appender instance, with its own ZMQ context.
	 * It will redirect all appended logs to the remote REP socket.
	 *
	 * @param url address (e.g. "tcp://host:123") of the remote REP socket
	 * @param source  name of the current Svarog instance for logging and crash reporting
	 */
	public ZmqRemoteAppender(String url, String source) {
		this.source = source;
		sender = new ZmqRemoteSender(url);
	}

	/**
	 * Add given log event to the queue, to be sent to the remote socket.
	 *
	 * @param le log event to be sent
	 */
	@Override
	protected void append(LoggingEvent le) {
		try {
			String message = createJsonMessage(le.getLevel().toString())
				.put("subsource", le.getLoggerName())
				.put("timestamp", le.getTimeStamp() / 1000)
				.put("msg", le.getMessage())
				.toString();
			sender.offer(message);
		} catch (JSONException ex) {
			// not really a possibility
		}
	}

	/**
	 * Send END_LOGS message to the remote socket.
	 */
	@Override
	public void close() {
		try {
			String message = createJsonMessage("end_logs").toString();
			sender.offer(message);
			sender.close();
		} catch (JSONException ex) {
			// not really a possibility
		}
	}

	private JSONObject createJsonMessage(String logType) throws JSONException {
		return (new JSONObject())
			.put("type", MESSAGE_TYPE)
			.put("log_type", logType)
			.put("source", source);
	}

	/**
	 * Always return false, since this appender does not require layout.
	 *
	 * @return false
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * Start the thread to send messages using ZMQ.
	 */
	public void startThread() {
		sender.startThread();
	}

}
