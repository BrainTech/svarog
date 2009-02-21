/* TestConnectionWorker.java created 2008-02-17
 * 
 */

package org.signalml.app.worker;

import javax.swing.SwingWorker;

import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.method.mp5.remote.MP5RemoteConnector;
import org.signalml.method.mp5.remote.TestConnectionRequest;
import org.signalml.method.mp5.remote.TestConnectionResponse;

/** TestConnectionWorker
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TestConnectionWorker extends SwingWorker<TestConnectionResponse, Void> {

	private TestConnectionRequest request;
	private String uri;
	private PleaseWaitDialog pleaseWaitDialog;
	
	public TestConnectionWorker(TestConnectionRequest request, String uri, PleaseWaitDialog pleaseWaitDialog) {
		this.request = request;
		this.uri = uri;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected TestConnectionResponse doInBackground() throws Exception {
				
		MP5RemoteConnector connector = MP5RemoteConnector.getSharedInstance();		
		return connector.testConnection(uri, request);

	}
	
	@Override
	protected void done() {
		
		pleaseWaitDialog.releaseIfOwnedBy(this);

	}

}
