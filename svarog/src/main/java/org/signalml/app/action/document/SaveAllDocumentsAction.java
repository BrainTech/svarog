/* SaveAllDocumentsAction.java created 2007-09-10
 *
 */
package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** SaveAllDocumentsAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SaveAllDocumentsAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SaveAllDocumentsAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public SaveAllDocumentsAction() {
		super();
		setText(_("Save All"));
		setIconPath("org/signalml/app/icon/save_all.png");
		setToolTip(_("Save all unsaved documents"));
		setMnemonic(KeyEvent.VK_S);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Save all documents");

		try {
			documentFlowIntegrator.saveAllDocuments();
		} catch (SignalMLException ex) {
			logger.error("Failed to save all documents", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to save all documents - i/o exception", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		if (documentFlowIntegrator != null) {
			setEnabled(documentFlowIntegrator.getDocumentManager().getDocumentCount() > 0);
		} else {
			setEnabled(false);
		}
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
