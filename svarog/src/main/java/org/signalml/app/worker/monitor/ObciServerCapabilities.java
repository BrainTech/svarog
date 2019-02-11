package org.signalml.app.worker.monitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.app.worker.monitor.messages.ObciServerCapabilitiesRequest;
import org.signalml.app.worker.monitor.messages.ObciServerCapabilitiesResponse;
import org.signalml.util.Util;

/**
 * This is the access point for checking capabilities of the OBCI server.
 * After calling initialize(), a thread will start which will try to
 * query the capabilities from the OBCI server. After that, it will exit,
 * and the capabilities will be available through has* methods.
 * Until the communication succeeds, all has* method will return false.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ObciServerCapabilities {

	private static final Logger logger = Logger.getLogger(ObciServerCapabilities.class);

	private static final int SLEEP_INTERVAL = 1000; // milliseconds
	
	private static final short CONSECUTIVE_FAILS_TO_LOG = 3;

	private static ObciServerCapabilities instance;

	private final Thread thread;
	private volatile Set<String> capabilities;
	private short failCount = 0;

	/**
	 * @return  the main (singleton) instance
	 */
	public synchronized static ObciServerCapabilities getSharedInstance() {
		if (instance == null) {
			instance = new ObciServerCapabilities();
		}
		return instance;
	}

	private ObciServerCapabilities() {
		thread = new Thread(() -> {
			while (true) {
				Util.sleep(SLEEP_INTERVAL);
				try {
					ObciServerCapabilitiesResponse response = (ObciServerCapabilitiesResponse) Helper.sendRequestAndParseResponse(
						new ObciServerCapabilitiesRequest(),
						Helper.getOpenBCIIpAddress(),
						Helper.getOpenbciPort(),
						MessageType.OBCI_SERVER_CAPABILITIES_RESPONSE
					);
					capabilities = new HashSet<>(Arrays.asList(response.capabilities));
					failCount = 0;
				} catch (Exception ex) {
					capabilities = null;
					if (failCount < CONSECUTIVE_FAILS_TO_LOG && ++failCount == CONSECUTIVE_FAILS_TO_LOG) {
						// this strange conditional prevents failCount to grow unconstrained (and, possibly, overflow)
						logger.error("could not fetch OBCI capabilities (tried three times)", ex);
					}
				}
			}
		});
		thread.setDaemon(true);
	}

	/**
	 * Initialize this object by starting a communication thread.
	 * Calling this method more than once does nothing.
	 */
	public void initialize() {
		thread.start();
	}

	public boolean hasCameraServer() {
		return hasCapability("camera_server");
	}

	public boolean hasOnlineAmplifiers() {
		return hasCapability("online_amplifiers");
	}

	public boolean hasOnlineExperiments() {
		return hasCapability("online_experiments");
	}

	public boolean hasPsychopyRunner() {
		return hasCapability("psychopy_runner");
	}

	public boolean hasVideoSaving() {
		return hasCapability("video_saving");
	}

	private boolean hasCapability(String name) {
		Set<String> caps = this.capabilities;
		return caps != null && caps.contains(name);
	}
}
