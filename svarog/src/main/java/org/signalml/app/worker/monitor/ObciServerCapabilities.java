package org.signalml.app.worker.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
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
	private int TIMEOUT = 1000;
	
	private static final short CONSECUTIVE_FAILS_TO_LOG = 3;

	private static ObciServerCapabilities instance;

	private final Thread thread;
	private Thread threadTrayStream;

	private volatile Set<String> capabilities;
	private short failCount = 0;
	private boolean triedToStartObciServer = false;

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
						MessageType.OBCI_SERVER_CAPABILITIES_RESPONSE,
						false, // means: do not handle exceptions
						1000
					);
					capabilities = new HashSet<>(Arrays.asList(response.capabilities));
					failCount = 0;
				} catch (Exception ex) {
					capabilities = null;
					if (!triedToStartObciServer)
					{
						launch_obci_tray();
					}
					//no obci ser
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
	
	private void launch_obci_tray()
	{
		triedToStartObciServer = true;
		TIMEOUT = 10000;
		String locale = Locale.getDefault().getLanguage();
		String[] variables = {"LANGUAGE="+locale,};
			
					
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("svarog_streamer", "--tray");
			Map<String, String> env = pb.environment();
			env.put("LANGUAGE", locale);
			pb = pb.redirectErrorStream(true);
		try {
			Process process = pb.start();
			InputStream is = process.getInputStream();
			
			threadTrayStream = new Thread(() -> {
				while (true) {
					try {
						is.read();
					} catch (IOException ex) {
						logger.error("couldn't read svarog_streamer pipe", ex);
					}
				}
				
			});
			threadTrayStream.setDaemon(true);
			threadTrayStream.start();
		} catch (IOException ex) {
			// it's ok if there isn't an obci_tray installed
			logger.warn("could not start acquisition server (svarog_streamer --tray)", ex);
		}
		
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
