/* OpenBookDocumentWorker.java created 2008-02-23
 *
 */
package org.signalml.app.worker;

import javax.swing.SwingWorker;

import org.signalml.app.document.BookDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.dialog.PleaseWaitDialog;

/** OpenBookDocumentWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenBookDocumentWorker extends SwingWorker<BookDocument, Void> {

	private OpenDocumentDescriptor descriptor;
	private PleaseWaitDialog pleaseWaitDialog;

	public OpenBookDocumentWorker(OpenDocumentDescriptor descriptor, PleaseWaitDialog pleaseWaitDialog) {
		this.descriptor = descriptor;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected BookDocument doInBackground() throws Exception {

		return new BookDocument(descriptor.getFile());
	}

	@Override
	protected void done() {

		pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
