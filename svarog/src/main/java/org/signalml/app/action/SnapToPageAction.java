/* SnapToPageAction.java created 2007-12-16
 *
 */

package org.signalml.app.action;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.signal.SignalView;

/** SnapToPageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SnapToPageAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SnapToPageAction.class);

	public SnapToPageAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Snap page to view"));
		setIconPath("org/signalml/app/icon/snaptopage.png");
		setToolTip(_("Resize time scale so that exactly one page fits in the view & align view on page boundary"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Snap to page");

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

		view.setSnapToPageMode(selected);

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

}
