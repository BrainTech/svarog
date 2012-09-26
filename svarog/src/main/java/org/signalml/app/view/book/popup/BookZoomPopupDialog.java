/* BookZoomPopupDialog.java created 2007-10-14
 *
 */

package org.signalml.app.view.book.popup;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.view.book.BookView;
import org.signalml.app.view.book.BookZoomSettingsPanel;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;

/** BookZoomPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookZoomPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private BookZoomSettingsPanel bookZoomSettingsPanel;

	public BookZoomPopupDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	public JComponent createInterface() {

		bookZoomSettingsPanel = new BookZoomSettingsPanel(true);

		return bookZoomSettingsPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		BookView view = (BookView) model;

		bookZoomSettingsPanel.fillPanelFromModel(view);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		BookView view = (BookView) model;

		bookZoomSettingsPanel.fillModelFromPanel(view);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookView.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean isFormClickApproving() {
		return true;
	}

}
