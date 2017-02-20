package org.signalml.app.logging;

import com.getsentry.raven.DefaultRavenFactory;
import com.getsentry.raven.Raven;
import com.getsentry.raven.RavenFactory;
import com.getsentry.raven.dsn.Dsn;
import com.getsentry.raven.log4j.SentryAppender;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.signalml.app.SvarogExceptionHandler;

/**
 * Configures Svarog logging system, depending on the environment.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SvarogLoggingConfigurer {

	private static final String OBCI_REP_IP = "127.0.0.1";
	private static final int OBCI_REP_PORT = 54654;
	private static final String SOURCE =
		"svarog_" + ZonedDateTime.now()
		.format(DateTimeFormatter.ISO_INSTANT)
		.replaceAll("[:\\-]", "");


	private static RavenFactory ravenFactory = new DefaultRavenFactory();

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
			ravenFactory = new ObciRavenFactory(url, SOURCE);
			ZmqRemoteAppender appender = new ZmqRemoteAppender(url, SOURCE);
			logger.removeAllAppenders();
			logger.addAppender(appender);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				appender.close();
			}));
			appender.startThread();
		} catch (IOException ex) {
			// socket connection failed, do nothing
		}
	}

	/**
	 * Configure additional Sentry handling.
	 * If Sentry messages are to be sent to OBCI, configure() must be called
	 * prior to configureSentry(). Otherwise, or if OBCI server is not running,
	 * messages will be sent directly to Sentry server.
	 *
	 * @param logger  Logger instance to be configured, usually the root logger
	 * @param dsn  DSN for Raven (e.g. https://sentry.io/...)
	 * @param site Site for raven - string installation name
	 */
	public static void configureSentry(Logger logger, String dsn, String site) {
		if (ravenFactory instanceof ObciRavenFactory) {
			// DSN does not matter if sending to OBCI
			dsn = "https://dummy:password@sentry/url";
		} else if (dsn == null || dsn.isEmpty()) {
			return;
		}
		try {
			Raven raven = ravenFactory.createRavenInstance(new Dsn(dsn));
			SvarogExceptionHandler.getSharedInstance().setRaven(raven);
			SentryAppender sentry = new SentryAppender(raven);
			sentry.setTags("site:"+site);
			sentry.setThreshold(Priority.FATAL);
			logger.addAppender(sentry);
			logger.info("successfully initialized logging to Sentry");
		} catch (Exception ex) {
			logger.error("cannot initialize logging to Sentry", ex);
		}
	}

}
