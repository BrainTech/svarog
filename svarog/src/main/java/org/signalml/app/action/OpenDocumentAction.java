/* OpenDocumentAction.java created 2007-09-10
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogI18n._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** OpenDocumentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenDocumentAction.class);

	private OpenDocumentDialog openDocumentDialog;
	private DocumentFlowIntegrator documentFlowIntegrator;

	public OpenDocumentAction() {
		super();
		setText(_("Open..."));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open a document from file"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		logger.debug("Open document");

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setMakeActive(true);

		boolean ok = openDocumentDialog.showDialog(ofd, true);
		if (!ok) {
			return;
		}

		documentFlowIntegrator.maybeOpenDocument(ofd);
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public OpenDocumentDialog getOpenDocumentDialog() {
		return openDocumentDialog;
	}

	public void setOpenDocumentDialog(OpenDocumentDialog openDocumentDialog) {
		if (openDocumentDialog == null) {
			throw new NullPointerException();
		}
		this.openDocumentDialog = openDocumentDialog;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		if (documentFlowIntegrator == null) {
			throw new NullPointerException();
		}
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
