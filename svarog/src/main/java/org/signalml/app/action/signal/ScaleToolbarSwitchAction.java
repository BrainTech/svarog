package org.signalml.app.action.signal;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Action to toggle scale toolbar on/off.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ScaleToolbarSwitchAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ScaleToolbarSwitchAction.class);

	public ScaleToolbarSwitchAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Scale toolbar"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Toolbar switch");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		signalDocument.getSignalView().toggleScaleToolBar();
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

}
