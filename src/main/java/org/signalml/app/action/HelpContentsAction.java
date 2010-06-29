/* HelpContentsAction.java created 2007-09-10
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.HelpDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** HelpContentsAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class HelpContentsAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(HelpContentsAction.class);

	private HelpDialog helpDialog;

	public HelpContentsAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.helpContents");
		setIconPath("org/signalml/app/icon/help.png");
		setToolTip("action.helpContentsToolTip");
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
