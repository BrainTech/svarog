package org.signalml.app.logging;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * ZeroMQ client sending string messages to a remote REP socket.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ZmqRemoteSender {

	/** maximal queue capacity to avoid exhausting memory */
	private static final int QUEUE_CAPACITY = 1000;

	/** timeout for sending pending messages at shutdown (milliseconds) */
	private static final int SHUTDOWN_TIMEOUT = 1000;

	private final LinkedBlockingQueue<String> queue;
	private final ZmqSendingThread thread;

	/**
	 * Create a new appender instance, with its own ZMQ context.
	 * It will redirect all appended logs to the remote REP socket.
	 *
	 * @param url address (e.g. "tcp://host:123") of the remote REP socket
	 */
	public ZmqRemoteSender(String url) {
		queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
		thread = new ZmqSendingThread(queue, url);
	}

	/**
	 * Add given message to the queue, to be sent to the remote socket.
	 *
	 * @param message message to be send
	 */
	protected void offer(String message) {
		queue.offer(message);
	}

	/**
	 * Finalize sending messages.
	 */
	public void close() {
		queue.offer("");
		try {
			// let's give the thread a chance to send pending messages
			thread.join(SHUTDOWN_TIMEOUT);
		} catch (InterruptedException ex) {
			// does not matter
		}
	}

	/**
	 * Start the thread to send messages using ZMQ.
	 */
	public final void startThread() {
		thread.start();
	}

}
