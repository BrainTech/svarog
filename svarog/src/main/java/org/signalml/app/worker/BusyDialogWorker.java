package org.signalml.app.worker;

import java.awt.Container;

import javax.print.CancelablePrintJob;
import javax.swing.SwingWorker;

import org.signalml.app.view.components.BusyDialog;

public class BusyDialogWorker extends SwingWorker {

	private BusyDialog busyDialog;
	private Container parentContainer;
	
	public BusyDialogWorker(Container parentContainer) {
		this.parentContainer = parentContainer;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		busyDialog = new BusyDialog(parentContainer);
		busyDialog.setVisible(true);
		return null;
	}
	
	public void cancel() {
		busyDialog.setVisible(false);
	}
}
