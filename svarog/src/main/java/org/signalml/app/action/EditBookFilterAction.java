/* EditBookFilterAction.java created 2008-03-04
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.model.BookFilterDescriptor;
import org.signalml.app.view.book.filter.BookFilterDialog;

/** EditBookFilterAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditBookFilterAction extends AbstractFocusableSignalMLAction<BookDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditBookFilterAction.class);

	private BookFilterDialog bookFilterDialog;

	public  EditBookFilterAction( BookDocumentFocusSelector bookDocumentFocusSelector) {
		super( bookDocumentFocusSelector);
		setText("action.bookFilter");
		setIconPath("org/signalml/app/icon/editbookfilter.png");
		setToolTip("action.bookFilterToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug("Book filter");

		BookDocument bookDocument = getActionFocusSelector().getActiveBookDocument();
		if (bookDocument == null) {
			logger.warn("Target document doesn't exist or is not a book");
			return;
		}

		BookFilterDescriptor descriptor = new BookFilterDescriptor(bookDocument.getFilterChain(), bookDocument);

		boolean ok = bookFilterDialog.showDialog(descriptor, true);
		if (!ok) {
			return;
		}

		bookDocument.setFilterChain(descriptor.getChain());

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveBookDocument() != null);
	}

	public BookFilterDialog getBookFilterDialog() {
		return bookFilterDialog;
	}

	public void setBookFilterDialog(BookFilterDialog bookFilterDialog) {
		this.bookFilterDialog = bookFilterDialog;
	}

}
