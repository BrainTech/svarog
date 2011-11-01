/* BookFilterSwitchAction.java created 2008-03-04
 *
 */

package org.signalml.app.action;

import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.domain.book.filter.AtomFilterChain;

/** BookFilterSwitchAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFilterSwitchAction extends AbstractFocusableSignalMLAction<BookDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(BookFilterSwitchAction.class);

	public  BookFilterSwitchAction( BookDocumentFocusSelector bookDocumentFocusSelector) {
		super( bookDocumentFocusSelector);
		setText("bookView.filterSwitch");
		setIconPath("org/signalml/app/icon/filter.png");
		setToolTip("bookView.filterSwitchToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Filter switch");

		BookDocument bookDocument = getActionFocusSelector().getActiveBookDocument();
		if (bookDocument == null) {
			logger.warn("Target document doesn't exist or is not a book");
			return;
		}

		ItemSelectable button = (ItemSelectable) ev.getSource();
		Object[] selectedObjects = button.getSelectedObjects();
		boolean selected = (selectedObjects != null && selectedObjects.length != 0);

		putValue(SELECTED_KEY, new Boolean(selected));
		AtomFilterChain chain = bookDocument.getFilterChain();
		if (selected != chain.isFilteringEnabled()) {
			chain = new AtomFilterChain(chain);
			chain.setFilteringEnabled(selected);
			bookDocument.setFilterChain(chain);
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveBookDocument() != null);
	}

}
