package org.signalml.app.view.common.dialogs;

/**
 * Dummy (no-op) implementation of IBusyDialog.
 * Allows for replacing BusyDialog in headless enviroment (e.g. tests).
 *
 * @author piotr.rozanski@braintech.pl
 */
public class DummyBusyDialog implements IBusyDialog {

	@Override
	public void dispose() {
		// nothing here
	}

	@Override
	public void setText(String text) {
		// nothing here
	}

	@Override
	public void setCancellable(boolean cancellable) {
		// nothing here
	}

	@Override
	public void setVisible(boolean visible) {
		// nothing here
	}

}
