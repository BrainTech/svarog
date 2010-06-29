/* SaveDocumentAsAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MutableDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** SaveDocumentAsAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SaveDocumentAsAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SaveDocumentAsAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public SaveDocumentAsAction(MessageSourceAccessor messageSource, DocumentFocusSelector documentFocusSelector) {
		super(messageSource, documentFocusSelector);
		setText("action.saveDocumentAs");
		setIconPath("org/signalml/app/icon/filesaveas.png");
		setToolTip("action.saveDocumentAsToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Save document as");

		Document document = getActionFocusSelector().getActiveDocument();
		if (document == null) {
			return;
		}

		try {
			documentFlowIntegrator.saveDocument(document, true);
		} catch (SignalMLException ex) {
			logger.error("Failed to save focused document as", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to save focused document as - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		Document document = getActionFocusSelector().getActiveDocument();
		if (document != null) {
			if (document instanceof MutableDocument) {
				enabled = true;
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
