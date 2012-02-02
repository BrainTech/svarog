/* HelpContentsAction.java created 2007-09-10
 *
 */
package org.signalml.app.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.signalml.app.view.components.dialogs.ErrorsDialog;
import org.signalml.app.view.components.dialogs.HelpDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** HelpContentsAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class HelpContentsAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(HelpContentsAction.class);

	private HelpDialog helpDialog;

	public HelpContentsAction() {
		super();
		setText(_("Contents..."));
		setIconPath("org/signalml/app/icon/help.png");
		setToolTip(_("Display help contents"));
		setMnemonic(KeyEvent.VK_C);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		if (helpDialog.isVisible()) {
			try {
				helpDialog.setPage(null);
			} catch (SignalMLException ex) {
				logger.error("Failed to set help page", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}
			helpDialog.toFront();
		} else {
			helpDialog.showDialog(null, true);
		}

	}

	public HelpDialog getHelpDialog() {
		return helpDialog;
	}

	public void setHelpDialog(HelpDialog helpDialog) {
		this.helpDialog = helpDialog;
	}

}
