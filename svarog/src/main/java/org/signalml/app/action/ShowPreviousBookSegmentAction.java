/* ShowPreviousBookSegmentAction.java created 2008-03-05
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.BookViewFocusSelector;
import org.signalml.app.view.book.BookView;

/** ShowPreviousBookSegmentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowPreviousBookSegmentAction extends AbstractFocusableSignalMLAction<BookViewFocusSelector> implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowPreviousBookSegmentAction.class);

	public  ShowPreviousBookSegmentAction( BookViewFocusSelector bookViewFocusSelector) {
		super( bookViewFocusSelector);
		setText("action.showPreviousBookSegment");
		setIconPath("org/signalml/app/icon/previousbooksegment.png");
		setToolTip("action.showPreviousBookSegmentToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		BookView bookView = getActionFocusSelector().getActiveBookView();
		if (bookView == null) {
			logger.warn("Target view doesn't exist");
			return;
		}

		bookView.showPreviousSegment();

	}

	@Override
	public void setEnabledAsNeeded() {

		boolean enabled = false;

		BookView view = getActionFocusSelector().getActiveBookView();
		if (view != null) {
			enabled = view.hasPreviousSegment();
		}

		setEnabled(enabled);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setEnabledAsNeeded();
	}

}
