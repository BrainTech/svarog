package org.signalml.app.action.signal;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Action to toggle recording toolbar on/off.
 *
 * @author ptr@mimuw.edu.pl
 */
public class RecordingToolbarSwitchAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RecordingToolbarSwitchAction.class);

	public RecordingToolbarSwitchAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Recording"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Toolbar switch");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument instanceof MonitorSignalDocument) {
			signalDocument.getSignalView().toggleRecordingToolBar();
		} else {
			logger.warn("Target document doesn't exist or is not a on-line signal");
			return;
		}
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() instanceof MonitorSignalDocument);
	}

}
