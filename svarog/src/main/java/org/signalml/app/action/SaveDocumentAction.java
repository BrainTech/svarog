/* SaveDocumentAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MutableDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;

/** SaveDocumentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SaveDocumentAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SaveDocumentAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public  SaveDocumentAction( DocumentFocusSelector documentFocusSelector) {
		super( documentFocusSelector);
		setText("action.saveDocument");
		setIconPath("org/signalml/app/icon/filesave.png");
		setToolTip("action.saveDocumentToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Save document");

		Document document = getActionFocusSelector().getActiveDocument();
		if (document == null) {
			return;
		}

		try {
			documentFlowIntegrator.saveDocument(document, false);
		} catch (SignalMLException ex) {
			logger.error("Failed to save focused document", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to save focused document - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

	}


	@Override
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		DocumentFocusSelector x = getActionFocusSelector();
		if (null != x) {
			Document document = x.getActiveDocument();
			if (document != null) {
				if (document instanceof MutableDocument) {
					if (!((MutableDocument) document).isSaved()) {
						enabled = true;
					}
				}
			}
		}
		setEnabled(enabled);
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
