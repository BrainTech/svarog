/* ShowPreviousBookChannelAction.java created 2008-03-05
 *
 */

package org.signalml.app.action.book;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.BookViewFocusSelector;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.book.BookView;

/** ShowPreviousBookChannelAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowPreviousBookChannelAction extends AbstractFocusableSignalMLAction<BookViewFocusSelector> implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowPreviousBookChannelAction.class);

	public ShowPreviousBookChannelAction(BookViewFocusSelector bookViewFocusSelector) {
		super(bookViewFocusSelector);
		setText(_("Previous channel"));
		setIconPath("org/signalml/app/icon/previousbookchannel.png");
		setToolTip(_("Navigate to previous channel"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		BookView bookView = getActionFocusSelector().getActiveBookView();
		if (bookView == null) {
			logger.warn("Target view doesn't exist");
			return;
		}

		bookView.showPreviousChannel();

	}

	@Override
	public void setEnabledAsNeeded() {

		boolean enabled = false;

		BookView view = getActionFocusSelector().getActiveBookView();
		if (view != null) {
			enabled = view.hasPreviousChannel();
		}

		setEnabled(enabled);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setEnabledAsNeeded();
	}

}
