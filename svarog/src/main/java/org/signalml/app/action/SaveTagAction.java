/* SaveTagAction.java created 2007-10-13
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

/** SaveTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SaveTagAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SaveTagAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public SaveTagAction( TagDocumentFocusSelector tagDocumentFocusSelector) {
		super( tagDocumentFocusSelector);
		setText(_("Save Tag"));
		setIconPath("org/signalml/app/icon/filesave.png");
		setToolTip(_("Save active tag"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Save tag");

		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if (tagDocument == null) {
			logger.warn("Target document doesn't exist");
			return;
		}

		try {
			documentFlowIntegrator.saveDocument(tagDocument, false);
		} catch (SignalMLException ex) {
			logger.error("Failed to save tag", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to save tag - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if (tagDocument != null && !tagDocument.isSaved()) {
			enabled = true;
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
