/* ActivateDocumentAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.Document;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.springframework.context.support.MessageSourceAccessor;

/** ActivateDocumentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActivateDocumentAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ActivateDocumentAction.class);

	private ActionFocusManager actionFocusManager;

	public ActivateDocumentAction(MessageSourceAccessor messageSource, ActionFocusManager actionFocusManager, DocumentFocusSelector documentFocusSelector) {
		super(messageSource, documentFocusSelector);
		this.actionFocusManager = actionFocusManager;
		setText("action.activateDocument");
		setIconPath("org/signalml/app/icon/activate.png");
		setToolTip("action.activateDocumentToolTip");
	}

	public ActivateDocumentAction(MessageSourceAccessor messageSource, ActionFocusManager actionFocusManager) {
		this(messageSource, actionFocusManager, actionFocusManager);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Activate focused document");

		Document document = getActionFocusSelector().getActiveDocument();
		if (document == null) {
			return;
		}

		if( (document instanceof SignalDocument) 
				|| (document instanceof MonitorSignalDocument) 
				|| (document instanceof BookDocument) ) {
			actionFocusManager.setActiveDocument(document);
		} else if (document instanceof TagDocument) {
			actionFocusManager.setActiveDocument(((TagDocument) document).getParent());
		}

	}

	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveDocument() != null);
	}

}
