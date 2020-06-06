/* CloseWindowAction.java created 2007-09-10
 *
 */
package org.signalml.app.action.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ViewFocusSelector;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.View;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** CloseWindowAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CloseWindowAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(CloseWindowAction.class);

	public CloseWindowAction() {
		super();
		setText(_("Exit"));
		setIconPath("org/signalml/app/icon/exit.png");
		setToolTip(_("Close the window and exit the aplication"));
		setMnemonic(KeyEvent.VK_X);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		ViewFocusSelector viewFocusSelector = (ViewFocusSelector) findFocusSelector(ev.getSource(), ViewFocusSelector.class);
		if (viewFocusSelector == null) {
			return;
		}
		View view = viewFocusSelector.getActiveView();
		if (view == null) {
			return;
		}

		view.closeView();

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

}
