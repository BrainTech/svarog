package org.signalml.app.worker;

import java.awt.Container;

import javax.swing.SwingWorker;

import org.signalml.app.view.components.BusyDialog;

public class BusyDialogWorker extends SwingWorker {

	private BusyDialog busyDialog;
	
	public BusyDialogWorker(Container parentContainer) {
		this.busyDialog = new BusyDialog(parentContainer);
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		busyDialog.setVisible(true);
		return null;
	}
	
	public void cancel() {
		busyDialog.setVisible(false);
	}
}
