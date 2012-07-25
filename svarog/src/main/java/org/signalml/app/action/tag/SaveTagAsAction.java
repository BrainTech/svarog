/* SaveTagAsAction.java created 2007-10-13
 *
 */
package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.plugin.export.SignalMLException;

/** SaveTagAsAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SaveTagAsAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SaveTagAsAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public SaveTagAsAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
		setText(_("Save Tag As..."));
		setIconPath("org/signalml/app/icon/filesaveas.png");
		setToolTip(_("Save active tag to another file"));
		setMnemonic(KeyEvent.VK_A);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Save tag as");

		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if (tagDocument == null) {
			logger.warn("Target document doesn't exist");
			return;
		}

		try {
			documentFlowIntegrator.saveDocument(tagDocument, true);
		} catch (SignalMLException ex) {
			logger.error("Failed to save document", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to save document - i/o exception", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		boolean enabled = false;
		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if (tagDocument != null) {
			enabled = true;
		}
		if (isTagDocumentAMonitorTagDocument(tagDocument))
			enabled = false;
		setEnabled(enabled);
	}


	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
