package org.signalml.peer;

/**
 * Information (PDO) on broker addresses, fetched by BrokerTcpConnector.
 * An instance of this class is necessary in order to create Peer object.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class BrokerInfo {

	public final String brokerURL;
	public final String[] pubURLs;
	public final String[] repURLs;

	/**
	 * Create a new BrokerInfo instance with given data.
	 *
	 * @param brokerURL  broker REP URL, e.g. tcp://1.2.3.4:535
	 * @param pubURLs  array of PUB URLs for peer
	 * @param repURLs  array of REP URLs for peer
	 */
	public BrokerInfo(String brokerURL, String[] pubURLs, String[] repURLs) {
		this.brokerURL = brokerURL;
		this.pubURLs = pubURLs;
		this.repURLs = repURLs;
	}

}
