/* ShowBottomPanelAction.java created 2007-12-16
 * 
 */
package org.signalml.app.action;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.view.View;
import org.springframework.context.support.MessageSourceAccessor;

/** ShowBottomPanelAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowBottomPanelAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(ShowBottomPanelAction.class);
			
	public ShowBottomPanelAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.showBottomPanel");
		setToolTip("action.showBottomPanelToolTip");		
		putValue(SELECTED_KEY, new Boolean(true));
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		logger.debug("Show bottom panel");
		
		ItemSelectable button = (ItemSelectable) ev.getSource();
		Object[] selectedObjects = button.getSelectedObjects();
		boolean selected = (selectedObjects != null && selectedObjects.length != 0 );
		putValue(SELECTED_KEY, new Boolean(selected));
		
		ViewFocusSelector viewFocusSelector = (ViewFocusSelector) findFocusSelector(ev.getSource(), ViewFocusSelector.class);
		if( viewFocusSelector == null ) {
			return;
		}
		View view = viewFocusSelector.getActiveView();
		if( view == null ) {
			return;
		}
		
		view.setBottomPanelVisible(selected);
			
	}
	
}
