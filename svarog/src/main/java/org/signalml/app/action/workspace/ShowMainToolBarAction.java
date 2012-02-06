/* ShowMainToolBarAction.java created 2007-12-16
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

/** ShowMainToolBarAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowMainToolBarAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowMainToolBarAction.class);

	public ShowMainToolBarAction() {
		super();
		setText(_("Show main toolbar"));
		setToolTip(_("Show main toolbar"));
		putValue(SELECTED_KEY, new Boolean(true));
		setMnemonic(KeyEvent.VK_M);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Show main toolbar");

		ItemSelectable button = (ItemSelectable) ev.getSource();
		Object[] selectedObjects = button.getSelectedObjects();
		boolean selected = (selectedObjects != null && selectedObjects.length != 0);
		putValue(SELECTED_KEY, new Boolean(selected));

		ViewFocusSelector viewFocusSelector = (ViewFocusSelector) findFocusSelector(ev.getSource(), ViewFocusSelector.class);
		if (viewFocusSelector == null) {
			logger.warn("No view selector");
			return;
		}
		View view = viewFocusSelector.getActiveView();
		if (view == null) {
			logger.warn("No view");
			return;
		}

		view.setMainToolBarVisible(selected);

	}

}
