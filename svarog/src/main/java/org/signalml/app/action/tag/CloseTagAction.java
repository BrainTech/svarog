/* CloseTagAction.java created 2007-10-14
 *
 */
package org.signalml.app.action.tag;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.TagDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
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

	public CloseTagAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
		setText(_("Close Tag"));
		setIconPath("org/signalml/app/icon/fileclose.png");
		setToolTip(_("Close active tag"));
		setMnemonic(KeyEvent.VK_C);
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
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to close document - i/o exception", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		TagDocument activeTagDocument = getActionFocusSelector().getActiveTagDocument();
		setEnabled(activeTagDocument != null && !isTagDocumentAMonitorTagDocument(activeTagDocument));
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
