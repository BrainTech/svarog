package org.signalml.app.action.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;

/**
 * Action to toggle scale toolbar on/off.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SettingsToolbarSwitchAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SettingsToolbarSwitchAction.class);

	public SettingsToolbarSwitchAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Settings"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Toolbar switch");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		signalDocument.getSignalView().toggleSettingsToolBar();
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

}
