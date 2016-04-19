package org.signalml.app.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.SignalView;

/**
 * @author ptr@mimuw.edu.pl
 */
public class DisplayClockTimeAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(DisplayClockTimeAction.class);

	public DisplayClockTimeAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Display clock time in signal"));
		setIconPath("org/signalml/app/icon/waiting.png");
		setToolTip(_("Display clock (absolute) time on time scale"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		SignalView view = (SignalView) signalDocument.getDocumentView();

		ItemSelectable button = (ItemSelectable) ev.getSource();
		Object[] selectedObjects = button.getSelectedObjects();
		boolean selected = (selectedObjects != null && selectedObjects.length != 0);
		putValue(SELECTED_KEY, new Boolean(selected));

		view.setDisplayClockTime(selected);
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

}
