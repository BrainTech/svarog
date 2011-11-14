/* ActivateSignalPageAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.SignalPageFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalView;

/** ActivateSignalPageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActivateSignalPageAction extends AbstractFocusableSignalMLAction<SignalPageFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ActivateSignalPageAction.class);

	private ActionFocusManager actionFocusManager;

	public ActivateSignalPageAction(ActionFocusManager actionFocusManager, SignalPageFocusSelector signalPageFocusSelector) {
		super(signalPageFocusSelector);
		this.actionFocusManager = actionFocusManager;
		setText(_("Show page"));
		setIconPath("org/signalml/app/icon/activate.png");
		setToolTip(_("Show signal page in viewer"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Activate focused signal page");

		SignalPageFocusSelector signalPageFocusSelector = getActionFocusSelector();

		SignalDocument signalDocument = signalPageFocusSelector.getActiveSignalDocument();
		if (signalDocument == null) {
			return;
		}
		int page = signalPageFocusSelector.getSignalPage();
		if (page < 0) {
			return;
		}

		SignalView signalView = (SignalView) signalDocument.getDocumentView();

		actionFocusManager.setActiveDocument(signalDocument);
		signalView.showTime(signalDocument.getPageSize() * page);

	}

	public void setEnabledAsNeeded() {
		SignalPageFocusSelector x = getActionFocusSelector();
		if (x != null)
			setEnabled(x.getSignalPage() >= 0);
	}

}
