package org.signalml.app.worker.monitor.messages;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class ObciServerCapabilitiesRequest extends LauncherMessage {

	public ObciServerCapabilitiesRequest() {
		super(MessageType.OBCI_SERVER_CAPABILITIES_REQUEST);
	}

}
