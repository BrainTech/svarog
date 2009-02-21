/* CreateCodecReaderWorker.java created 2007-10-18
 * 
 */

package org.signalml.app.worker;

import javax.swing.SwingWorker;

import org.signalml.app.view.dialog.PleaseWaitDialog;
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
