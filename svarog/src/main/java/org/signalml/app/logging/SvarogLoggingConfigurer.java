package org.signalml.app.logging;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.log4j.Logger;

/**
 * Configures Svarog logging system, depending on the environment.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SvarogLoggingConfigurer {

	private static final String OBCI_REP_IP = "127.0.0.1";
	private static final int OBCI_REP_PORT = 54654;

	private SvarogLoggingConfigurer() {
		// noninstantiable class
	}

	/**
	 * Configure Svarog logging appender in an adaptive way.
	 * If obci_server is running, redirect all logs to it using ZMQ.
	 * Otherwise, leave the current configuration unchanged.
	 *
	 * @param logger  Logger instance to be configured, usually the root logger
	 */
	public static void configure(Logger logger) {
		try {
			try (java.net.Socket socket = new java.net.Socket()) {
				socket.connect(new InetSocketAddress(OBCI_REP_IP, OBCI_REP_PORT));
			}
			// obci_server appears to be running
			String url = "tcp://"+OBCI_REP_IP+":"+OBCI_REP_PORT;
			ZmqRemoteAppender appender = new ZmqRemoteAppender(url);
			logger.removeAllAppenders();
			logger.addAppender(appender);
			appender.startThread();
		} catch (IOException ex) {
			// socket connection failed, do nothing
		}
	}

}
