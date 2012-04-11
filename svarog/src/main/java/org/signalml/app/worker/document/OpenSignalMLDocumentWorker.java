/* OpenSignalMLDocumentWorker.java created 2007-10-18
 *
 */

package org.signalml.app.worker.document;

import java.io.File;

import javax.swing.SwingWorker;

import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.view.components.dialogs.PleaseWaitDialog;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.domain.montage.SignalConfigurer;

/** OpenSignalMLDocumentWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenSignalMLDocumentWorker extends SwingWorker<SignalMLDocument, Void> {

	private SignalMLCodec codec;
	private File file;
	private PleaseWaitDialog pleaseWaitDialog;

	public OpenSignalMLDocumentWorker(OpenDocumentDescriptor descriptor, PleaseWaitDialog pleaseWaitDialog) {
		SignalMLDescriptor signalmDescriptor = (SignalMLDescriptor) descriptor.getOpenSignalDescriptor();
		this.codec = signalmDescriptor.getCodec();
		this.pleaseWaitDialog = pleaseWaitDialog;
		this.file = descriptor.getFile();
	}
	
	public OpenSignalMLDocumentWorker(SignalMLCodec codec, File file) {
		this.codec = codec;
		this.file = file;
	}

	@Override
	protected SignalMLDocument doInBackground() throws Exception {

		SignalMLCodecReader reader = codec.createReader();

		SignalMLDocument signalMLDocument = new SignalMLDocument(reader);
		signalMLDocument.setBackingFile(file);

		signalMLDocument.openDocument();

		return signalMLDocument;
	}

	@Override
	protected void done() {

		if (pleaseWaitDialog != null)
			pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
