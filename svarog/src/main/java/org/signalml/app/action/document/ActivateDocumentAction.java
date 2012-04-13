/* ActivateDocumentAction.java created 2007-10-15
 *
 */
package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.plugin.export.signal.Document;

/** ActivateDocumentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActivateDocumentAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ActivateDocumentAction.class);

	private ActionFocusManager actionFocusManager;

	public ActivateDocumentAction(ActionFocusManager actionFocusManager, DocumentFocusSelector documentFocusSelector) {
		super(documentFocusSelector);
		this.actionFocusManager = actionFocusManager;
		setText(_("Show"));
		setIconPath("org/signalml/app/icon/activate.png");
		setToolTip(_("Show document"));
	}

	public ActivateDocumentAction(ActionFocusManager actionFocusManager) {
		this(actionFocusManager, actionFocusManager);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Activate focused document");

		Document document = getActionFocusSelector().getActiveDocument();
		if (document == null) {
			return;
		}

		if ((document instanceof SignalDocument)
				|| (document instanceof MonitorSignalDocument)
				|| (document instanceof BookDocument)) {
			actionFocusManager.setActiveDocument(document);
		} else if (document instanceof TagDocument) {
			actionFocusManager.setActiveDocument(((TagDocument) document).getParent());
		}

	}

	public void setEnabledAsNeeded() {
		DocumentFocusSelector x = getActionFocusSelector();
		if (null != x)
			setEnabled(x.getActiveDocument() != null);
	}

}
