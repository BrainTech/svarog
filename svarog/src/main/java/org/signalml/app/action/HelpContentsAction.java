/* HelpContentsAction.java created 2007-09-10
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.net.URL;
import org.apache.log4j.Logger;
import org.signalml.app.view.common.dialogs.HelpDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
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

	private final URL url;

	public HelpContentsAction(String text, URL url) {
		super();
		this.url = url;
		setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		if (helpDialog.isVisible()) {
			try {
				helpDialog.setPage(url);
			} catch (SignalMLException ex) {
				logger.error("Failed to set help page", ex);
				Dialogs.showExceptionDialog((Window) null, ex);
				return;
			}
			helpDialog.toFront();
		} else {
			helpDialog.showDialog(url, true);
		}

	}

	public HelpDialog getHelpDialog() {
		return helpDialog;
	}

	public void setHelpDialog(HelpDialog helpDialog) {
		this.helpDialog = helpDialog;
	}

}
