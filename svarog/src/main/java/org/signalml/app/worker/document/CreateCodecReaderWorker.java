/* CreateCodecReaderWorker.java created 2007-10-18
 *
 */

package org.signalml.app.worker.document;

import javax.swing.SwingWorker;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecReader;

/** CreateCodecReaderWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CreateCodecReaderWorker extends SwingWorker<SignalMLCodecReader, Void> {

	private SignalMLCodec codec;
	private PleaseWaitDialog pleaseWaitDialog;

	public CreateCodecReaderWorker(SignalMLCodec codec, PleaseWaitDialog pleaseWaitDialog) {
		assert codec != null;
		assert pleaseWaitDialog != null;
		this.codec = codec;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected SignalMLCodecReader doInBackground() throws Exception {

		return codec.createReader();

	}

	@Override
	protected void done() {

		pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
