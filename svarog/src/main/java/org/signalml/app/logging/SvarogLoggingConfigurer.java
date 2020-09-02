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
import org.signalml.app.worker.monitor.ObciServerCapabilities;
import org.signalml.util.SvarogConstants;

/**
 * Configures Svarog logging system, depending on the environment.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SvarogLoggingConfigurer {

	//Svarog configuration doesn't exist yet.
	private static final String OBCI_REP_IP = "127.0.0.1";
	private static final int OBCI_REP_PORT = 12012;
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
		// full obci server which can accept logs
		if(ObciServerCapabilities.getSharedInstance().hasOnlineExperiments())
		{
			String url = "tcp://"+OBCI_REP_IP+":"+OBCI_REP_PORT;
			ravenFactory = new ObciRavenFactory(url, SOURCE);
			ZmqRemoteAppender appender = new ZmqRemoteAppender(url, SOURCE);
			logger.removeAllAppenders();
			logger.addAppender(appender);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				appender.close();
			}));
			appender.startThread();
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
			Dsn finalDsn = new Dsn(dsn);
			Raven raven = ravenFactory.createRavenInstance(finalDsn);

			raven.addBuilderHelper(eventBuilder -> {
				eventBuilder.withRelease(SvarogConstants.VERSION);
			});

			raven.addBuilderHelper(eventBuilder -> {
				eventBuilder.withTag("site", "Svarog");
			});

			raven.addBuilderHelper(eventBuilder -> {
				eventBuilder.withTag("usersite", site);
			});

			SvarogExceptionHandler.getSharedInstance().setRaven(raven);
			SentryAppender sentry = new SentryAppender(raven);
			sentry.setTags("usersite:"+site);
			sentry.setTags("site:"+"Svarog");

			sentry.setThreshold(Priority.FATAL);
			sentry.setRelease(SvarogConstants.VERSION);
			sentry.setName("Svarog");
			logger.addAppender(sentry);
			logger.info("successfully initialized logging to Sentry");
		} catch (Exception ex) {
			logger.error("cannot initialize logging to Sentry", ex);
		}
	}

}
