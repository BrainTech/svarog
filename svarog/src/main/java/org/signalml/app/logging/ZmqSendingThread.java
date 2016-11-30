package org.signalml.app.logging;

import java.util.concurrent.BlockingQueue;
import org.zeromq.ZMQ;

/**
 * Sends messages from blocking queue to given REP (ØMQ socket) address.
 *
 * @author piotr.rozanski@braintech.pl
 */
class ZmqSendingThread extends Thread {

	private static final int TIMEOUT = 2000; // milliseconds

	private final BlockingQueue<String> queue;
	private final String url;

	/**
	 * Create a new thread, but do not start it immediately.
	 * To start the thread, {@link #start()} must be called.
	 *
	 * @param queue  blocking queue instance to read messages from
	 * @param url address (e.g. "tcp://host:123") of the remote REP socket
	 */
	public ZmqSendingThread(BlockingQueue<String> queue, String url) {
		this.queue = queue;
		this.url = url;
		setDaemon(true);
	}

	private String takeFromQueue() {
		while (true) try {
			return queue.take();
		} catch (InterruptedException ex) {
			// try again
		}
	}

	@Override
	public void run() {
		String message = takeFromQueue();
		try (ZMQ.Context context = ZMQ.context(1)) {
			while (true) {
				try (ZMQ.Socket socket = context.socket(ZMQ.REQ)) {
					socket.setLinger(0);
					socket.setHWM(100);
					socket.setSendTimeOut(TIMEOUT);
					socket.setReceiveTimeOut(TIMEOUT);
					socket.connect(url);
					while (true) {
						if (message.isEmpty()) {
							// empty message in the queue means
							// the thread is to be terminated
							return;
						}
						if (!socket.send(message) || socket.recv() == null) {
							break; // timeout, re-create socket
						}
						// message sent, take next one
						message = takeFromQueue();
					}
				}
			}
		}
	}

}
