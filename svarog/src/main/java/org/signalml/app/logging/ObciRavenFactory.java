package org.signalml.app.logging;

import com.getsentry.raven.DefaultRavenFactory;
import com.getsentry.raven.connection.Connection;
import com.getsentry.raven.connection.HttpConnection;
import com.getsentry.raven.dsn.Dsn;
import com.getsentry.raven.marshaller.Marshaller;

/**
 * Custom Raven factory for use with OBCI.
 * When asked to create a HTTP(S)-based Raven client, it will create
 * a OBCI client using the same format instead.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ObciRavenFactory extends DefaultRavenFactory {

	private final String obciRepUrl;

	/**
	 * Create a new factory.
	 *
	 * @param obciRepUrl  address (e.g. "tcp://host:123") of the remote REP socket
	 */
	public ObciRavenFactory(String obciRepUrl) {
		this.obciRepUrl = obciRepUrl;
	}

	/**
	 * Creates an OBCI ZMQ connection as a fake HTTP connection
	 *
	 * @param dsn Data Source Name of the Sentry server.
	 * @return an {@link HttpConnection} to the server.
	 */
	@Override
	protected Connection createHttpConnection(Dsn dsn) {
		ObciConnection connection = new ObciConnection(obciRepUrl);
		Marshaller marshaller = createMarshaller(dsn);
		connection.setMarshaller(marshaller);
		return connection;
	}

	/**
	 * Always return false (no compression).
	 * This method is overriden in order to facilitate parsing of generated
	 * JSON outputs.
	 *
	 * @param dsn  ignored
	 * @return false
	 */
	@Override
	protected boolean getCompressionEnabled(Dsn dsn) {
		return false;
	}

}
