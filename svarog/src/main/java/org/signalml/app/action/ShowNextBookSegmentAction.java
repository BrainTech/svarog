/* ShowNextBookSegmentAction.java created 2008-03-05
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogI18n._;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.BookViewFocusSelector;
import org.signalml.app.view.book.BookView;

/** ShowNextBookSegmentAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowNextBookSegmentAction extends AbstractFocusableSignalMLAction<BookViewFocusSelector> implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowNextBookSegmentAction.class);

	public ShowNextBookSegmentAction(BookViewFocusSelector bookViewFocusSelector) {
		super(bookViewFocusSelector);
		setText(_("Next segment"));
		setIconPath("org/signalml/app/icon/nextbooksegment.png");
		setToolTip(_("Navigate to next segment"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		BookView bookView = getActionFocusSelector().getActiveBookView();
		if (bookView == null) {
			logger.warn("Target view doesn't exist");
			return;
		}

		bookView.showNextSegment();

	}

	@Override
	public void setEnabledAsNeeded() {

		boolean enabled = false;

		BookView view = getActionFocusSelector().getActiveBookView();
		if (view != null) {
			enabled = view.hasNextSegment();
		}

		setEnabled(enabled);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setEnabledAsNeeded();
	}

}
