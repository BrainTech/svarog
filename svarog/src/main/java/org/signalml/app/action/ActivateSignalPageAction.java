/* ActivateSignalPageAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.SignalPageFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.springframework.context.support.MessageSourceAccessor;

/** ActivateSignalPageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActivateSignalPageAction extends AbstractFocusableSignalMLAction<SignalPageFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ActivateSignalPageAction.class);

	private ActionFocusManager actionFocusManager;

	public ActivateSignalPageAction(MessageSourceAccessor messageSource, ActionFocusManager actionFocusManager, SignalPageFocusSelector signalPageFocusSelector) {
		super(messageSource, signalPageFocusSelector);
		this.actionFocusManager = actionFocusManager;
		setText("action.activateSignalPage");
		setIconPath("org/signalml/app/icon/activate.png");
		setToolTip("action.activateSignalPageToolTip");
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
		setEnabled(getActionFocusSelector().getSignalPage() >= 0);
	}

}
