package org.signalml.app.logging;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;
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

	/** maximal queue capacity to avoid exhausting memory */
	private static final int QUEUE_CAPACITY = 1000;

	/** timeout for sending pending messages at shutdown (milliseconds) */
	private static final int SHUTDOWN_TIMEOUT = 1000;

	private final String source;
	private final LinkedBlockingQueue<String> queue;
	private final ZmqLoggingThread thread;

	/**
	 * Create a new appender instance, with its own ZMQ context.
	 * It will redirect all appended logs to the remote REP socket.
	 *
	 * @param url address (e.g. "tcp://host:123") of the remote REP socket
	 */
	public ZmqRemoteAppender(String url) {
		source = "svarog_" + ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
		thread = new ZmqLoggingThread(queue, url);
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
			queue.offer(message);
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
			queue.offer(message);
			queue.offer("");
			try {
				// let's give the thread a chance to send these messages
				thread.join(SHUTDOWN_TIMEOUT);
			} catch (InterruptedException ex) {
				// does not matter
			}
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
		thread.start();
	}

}
