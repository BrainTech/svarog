/* ViewModeAction.java created 2007-09-10
 *
 */
package org.signalml.app.action.workspace;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.view.View;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** ViewModeAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewModeAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ViewModeAction.class);

	public ViewModeAction() {
		super();
		setText(_("View mode"));
		setIconPath("org/signalml/app/icon/viewmode.png");
		setToolTip(_("Maximize the document view (see preferences for settings)"));
		setMnemonic(KeyEvent.VK_V);

	}
	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Maximize documents");

		ItemSelectable button = (ItemSelectable) ev.getSource();
		Object[] selectedObjects = button.getSelectedObjects();
		boolean selected = (selectedObjects != null && selectedObjects.length != 0);
		putValue(SELECTED_KEY, new Boolean(selected));

		ViewFocusSelector viewFocusSelector = (ViewFocusSelector) findFocusSelector(ev.getSource(), ViewFocusSelector.class);
		if (viewFocusSelector == null) {
			return;
		}
		View view = viewFocusSelector.getActiveView();
		if (view == null) {
			return;
		}

		view.setViewMode(selected);

	}

}
