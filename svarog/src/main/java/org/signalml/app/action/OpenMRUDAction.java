/* OpenMRUDAction.java created 2007-10-15
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.MRUDFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.plugin.export.SignalMLException;

/** OpenMRUDAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenMRUDAction extends AbstractFocusableSignalMLAction<MRUDFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenMRUDAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;

	public OpenMRUDAction( MRUDFocusSelector mrudFocusSelector) {
		super( mrudFocusSelector);
		setText(_("Open"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open recently used document"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Open focused MRUD");

		MRUDEntry entry = getActionFocusSelector().getActiveMRUDEntry();
		if (entry == null) {
			return;
		}

		try {
			documentFlowIntegrator.openMRUDEntry(entry);
		} catch (SignalMLException ex) {
			logger.error("Failed to open mrud", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to open mrud - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch (ConnectException ex) {
			logger.error("Failed to open mrud - connection exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;		 
		}

	}



	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveMRUDEntry() != null);
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
