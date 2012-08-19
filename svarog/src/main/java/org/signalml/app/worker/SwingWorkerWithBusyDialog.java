package org.signalml.app.worker;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.signalml.app.view.common.dialogs.BusyDialog;

public abstract class SwingWorkerWithBusyDialog<T, S> extends SwingWorker<T, S> implements PropertyChangeListener {

	private BusyDialog busyDialog;
	/**
	 * This variable is for synchronity reasons. Without it, there could be a
	 * situation when busyDialog.setVisible(false) could be invoked before
	 * busyDialog.setVisible(true).
	 */
	private boolean busyDialogVisible = true;
	/**
	 * This variable determines if this SwingWorker should show
	 * the busy dialog.
	 */
	private boolean busyDialogShouldBeShown = true;

	public SwingWorkerWithBusyDialog(Container parentContainer) {
		super();
		this.busyDialog = new BusyDialog(parentContainer);
		busyDialog.addPropertyChangeListener(this);
	}

	public BusyDialog getBusyDialog() {
		return busyDialog;
	}

	protected void showBusyDialog() {
		if (!busyDialogShouldBeShown)
			return;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BusyDialog.CANCEL_BUTTON_PRESSED)) {
			this.cancel(true);
		}
	}

	/**
	 * If this method is used to set that the busy dialog should not be shown,
	 * it is not shown even if the {@link SwingWorkerWithBusyDialog#showBusyDialog()}
	 * is invoked.
	 * @param busyDialogShouldBeShown a variable determining if the busy dialog
	 * should be shown.
	 */
	public void setBusyDialogShouldBeShown(boolean busyDialogShouldBeShown) {
		this.busyDialogShouldBeShown = busyDialogShouldBeShown;
	}

}
