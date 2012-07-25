/* SaveDocumentWorker.java created 2007-10-18
 *
 */

package org.signalml.app.worker.document;

import javax.swing.SwingWorker;

import org.signalml.app.document.MutableDocument;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;

/** SaveDocumentWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SaveDocumentWorker extends SwingWorker<Void, Void> {

	private MutableDocument document;
	private PleaseWaitDialog pleaseWaitDialog;

	public SaveDocumentWorker(MutableDocument document, PleaseWaitDialog pleaseWaitDialog) {
		this.document = document;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected Void doInBackground() throws Exception {

		document.saveDocument();
		return null;

	}

	@Override
	protected void done() {

		pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
