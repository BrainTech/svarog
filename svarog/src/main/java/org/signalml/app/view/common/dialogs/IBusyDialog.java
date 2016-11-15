package org.signalml.app.view.common.dialogs;

/**
 * Interface for both BusyDialog and DummyBusyDialog.
 * Allows for replacing BusyDialog with DummyBusyDialog
 * in headless enviroment (e.g. tests).
 *
 * @author piotr.rozanski@braintech.pl
 */
public interface IBusyDialog {

	public void dispose();

	public void setText(String text);

	public void setCancellable(boolean cancellable);

	public void setVisible(boolean visible);

}
