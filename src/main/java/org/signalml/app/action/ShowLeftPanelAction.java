/* ShowLeftPanelAction.java created 2007-12-16
 *
 */
package org.signalml.app.action;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.view.View;
import org.springframework.context.support.MessageSourceAccessor;

/** ShowLeftPanelAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowLeftPanelAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowLeftPanelAction.class);

	public ShowLeftPanelAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.showLeftPanel");
		setToolTip("action.showLeftPanelToolTip");
		putValue(SELECTED_KEY, new Boolean(true));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Show left panel");

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

		view.setLeftPanelVisible(selected);

	}

}
