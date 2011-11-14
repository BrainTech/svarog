/* CloseDocumentAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.plugin.export.signal.Document;

/** CloseDocumentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CloseDocumentAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(CloseDocumentAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public CloseDocumentAction(DocumentFocusSelector documentFocusSelector) {
		super(documentFocusSelector);
		setText(_("Close"));
		setIconPath("org/signalml/app/icon/fileclose.png");
		setToolTip(_("Close the active document"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Close document");

		Document document = getActionFocusSelector().getActiveDocument();
		if (document == null) {
			return;
		}

		documentFlowIntegrator.closeDocumentAndHandleExceptions(document);

	}

	public void setEnabledAsNeeded() {
		DocumentFocusSelector x = getActionFocusSelector();
		if (null != x)
			setEnabled(x.getActiveDocument() != null);
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
