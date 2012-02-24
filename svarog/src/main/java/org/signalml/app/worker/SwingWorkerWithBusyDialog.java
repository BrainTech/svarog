package org.signalml.app.worker;

import java.awt.Container;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.signalml.app.view.components.BusyDialog;

public abstract class SwingWorkerWithBusyDialog<T, S> extends SwingWorker<T, S> {

	private BusyDialog busyDialog;
	/**
	 * This variable is for synchronity reasons. Without it, there could be a
	 * situation when busyDialog.setVisible(false) could be invoked before
	 * busyDialog.setVisible(true).
	 */
	private boolean busyDialogVisible = true;

	public SwingWorkerWithBusyDialog(Container parentContainer) {
		super();
		this.busyDialog = new BusyDialog(parentContainer);
	}

	protected void showBusyDialog() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				busyDialog.setVisible(busyDialogVisible);
			}
		});
	}

	protected void hideBusyDialog() {
		busyDialogVisible = false;
		busyDialog.setVisible(busyDialogVisible);
	}

	@Override
	protected void done() {
		hideBusyDialog();
		busyDialog.dispose();
	}

}
