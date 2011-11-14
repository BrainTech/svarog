/* CloseTagAction.java created 2007-10-14
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.plugin.export.SignalMLException;

/** CloseTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CloseTagAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(CloseTagAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public  CloseTagAction( TagDocumentFocusSelector tagDocumentFocusSelector) {
		super( tagDocumentFocusSelector);
		setText(_("Close Tag"));
		setIconPath("org/signalml/app/icon/fileclose.png");
		setToolTip(_("Close active tag"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Close tag");

		TagDocument activeTag = getActionFocusSelector().getActiveTagDocument();
		if (activeTag == null) {
			logger.warn("Active tag doesn't exist");
			return;
		}

		try {
			documentFlowIntegrator.closeDocument(activeTag, false, false);
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

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTagDocument() != null);
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
