package org.signalml.app.worker.monitor.exceptions;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.view.components.dialogs.errors.Dialogs;

public class OpenbciConnectionException extends OpenbciCommunicationException {

	private String ipAddress;
	private int port;

	public OpenbciConnectionException(String errorMessage) {
		super(errorMessage);
	}

	public OpenbciConnectionException(String errorMessage, String ipAddress, int port) {
		super(errorMessage);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns true, if this exception happened while trying to connect
	 * to openBCI, false - if it tried to connect to an experiment.
	 * @return
	 */
	private boolean triedToConnectToOpenBCIDaemon() {
		ApplicationConfiguration applicationConfiguration = SvarogApplication.getSharedInstance().getApplicationConfiguration();
		if (ipAddress.equalsIgnoreCase(applicationConfiguration.getOpenbciIPAddress())
				&& port == applicationConfiguration.getOpenbciPort()) {
			return true;
		}
		return false;
	}

	@Override
	public void showErrorDialog(String defaultMessage) {
		String msg2;
		if (triedToConnectToOpenBCIDaemon())
			msg2 = _("Is openBCI daemon running?");
		else
			msg2 = _("Is the selected experiment running?");

		Dialogs.showError(_R("({0}) {1} ", getMessage(), msg2));
	}

}
