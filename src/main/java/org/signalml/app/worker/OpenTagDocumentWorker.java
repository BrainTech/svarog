/* OpenTagDocumentWorker.java created 2007-10-18
 *
 */

package org.signalml.app.worker;

import javax.swing.SwingWorker;

import org.signalml.app.document.TagDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.dialog.PleaseWaitDialog;

/** OpenTagDocumentWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenTagDocumentWorker extends SwingWorker<TagDocument, Void> {

	private OpenDocumentDescriptor descriptor;
	private PleaseWaitDialog pleaseWaitDialog;

	public OpenTagDocumentWorker(OpenDocumentDescriptor descriptor, PleaseWaitDialog pleaseWaitDialog) {
		this.descriptor = descriptor;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected TagDocument doInBackground() throws Exception {

		return new TagDocument(descriptor.getFile());
	}

	@Override
	protected void done() {

		pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
