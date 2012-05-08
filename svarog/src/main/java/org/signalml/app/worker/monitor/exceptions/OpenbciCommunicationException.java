package org.signalml.app.worker.monitor.exceptions;

import static org.signalml.app.util.i18n.SvarogI18n._R;

import org.signalml.app.view.components.dialogs.errors.Dialogs;

public class OpenbciCommunicationException extends Exception {

	public OpenbciCommunicationException(String errorMessage) {
		super(errorMessage);
	}

	public void showErrorDialog(String defaultMessage) {
		Dialogs.showError(_R("{0} ({1})", defaultMessage, getMessage()));
	}
}
