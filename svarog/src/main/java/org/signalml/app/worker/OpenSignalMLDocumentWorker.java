/* OpenSignalMLDocumentWorker.java created 2007-10-18
 *
 */

package org.signalml.app.worker;

import javax.swing.SwingWorker;

import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.codec.SignalMLCodecReader;

/** OpenSignalMLDocumentWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenSignalMLDocumentWorker extends SwingWorker<SignalMLDocument, Void> {

	private OpenDocumentDescriptor descriptor;
	private PleaseWaitDialog pleaseWaitDialog;

	public OpenSignalMLDocumentWorker(OpenDocumentDescriptor descriptor, PleaseWaitDialog pleaseWaitDialog) {
		this.descriptor = descriptor;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected SignalMLDocument doInBackground() throws Exception {

		SignalMLCodecReader reader = descriptor.getOpenSignalDescriptor().getOpenFileSignalDescriptor().getCodec().createReader();

		SignalMLDocument signalMLDocument = new SignalMLDocument(reader, descriptor.getOpenSignalDescriptor().getOpenFileSignalDescriptor().getType());
		signalMLDocument.setBackingFile(descriptor.getOpenSignalDescriptor().getOpenFileSignalDescriptor().getFile());

		signalMLDocument.openDocument();

		return signalMLDocument;
	}

	@Override
	protected void done() {

		pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
