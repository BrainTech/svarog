/* CloseDocumentAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.app.worker.processes.ProcessManager;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.springframework.context.support.MessageSourceAccessor;

/** CloseDocumentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CloseDocumentAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(CloseDocumentAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public CloseDocumentAction(MessageSourceAccessor messageSource, DocumentFocusSelector documentFocusSelector) {
		super(messageSource, documentFocusSelector);
		setText("action.closeDocument");
		setIconPath("org/signalml/app/icon/fileclose.png");
		setToolTip("action.closeDocumentToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Close document");

		Document document = getActionFocusSelector().getActiveDocument();
		if (document == null) {
			return;
		}

		try {
			documentFlowIntegrator.closeDocument(document, false, false);
                        if (document instanceof MonitorSignalDocument) {
                                MonitorSignalDocument signalDocument = (MonitorSignalDocument) document;
                                if (signalDocument.getOpenMonitorDescriptor().getSignalSource().isAmplifier()) {
                                        ProcessManager.getInstance().killAll();
                                }
                        }
		} catch (SignalMLException ex) {
			logger.error("Failed to close document", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to close document - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

	}

	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveDocument() != null);
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
