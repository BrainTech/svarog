/* ZoomSettingsPopupDialog.java created 2007-10-14
 *
 */

package org.signalml.app.view.signal.popup;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.view.element.SignalZoomSettingsPanel;
import org.signalml.app.view.signal.ZoomSignalTool;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;

/** ZoomSettingsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomSettingsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private SignalZoomSettingsPanel signalZoomSettingsPanel;

	public ZoomSettingsPopupDialog() {
		super();
	}

	public ZoomSettingsPopupDialog( Window w, boolean isModal) {
		super( w, isModal);
	}

	@Override
	public JComponent createInterface() {

		signalZoomSettingsPanel = new SignalZoomSettingsPanel( true);

		return signalZoomSettingsPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		ZoomSignalTool tool = (ZoomSignalTool) model;

		signalZoomSettingsPanel.fillPanelFromModel(tool.getSettings());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		ZoomSignalTool tool = (ZoomSignalTool) model;

		signalZoomSettingsPanel.fillModelFromPanel(tool.getSettings());
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ZoomSignalTool.class.isAssignableFrom(clazz);
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
