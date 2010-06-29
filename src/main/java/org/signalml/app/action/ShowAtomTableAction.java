/* ShowAtomTableAction.java created 2008-03-04
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.view.book.AtomTableDialog;
import org.signalml.app.view.book.BookView;
import org.springframework.context.support.MessageSourceAccessor;

/** ShowAtomTableAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ShowAtomTableAction extends AbstractFocusableSignalMLAction<BookDocumentFocusSelector> implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ShowAtomTableAction.class);

	private AtomTableDialog atomTableDialog;

	public ShowAtomTableAction(MessageSourceAccessor messageSource, BookDocumentFocusSelector bookDocumentFocusSelector) {
		super(messageSource, bookDocumentFocusSelector);
		setText("action.showAtomTable");
		setIconPath("org/signalml/app/icon/atomtable.png");
		setToolTip("action.showAtomTableToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		BookDocument bookDocument = getActionFocusSelector().getActiveBookDocument();
		if (bookDocument == null) {
			logger.warn("Target document doesn't exist or is not a book");
			return;
		}

		BookView bookView = (BookView) bookDocument.getDocumentView();

		atomTableDialog.showDialog(bookView, true);

		bookView.getPlot().repaint();

	}

	@Override
	public void setEnabledAsNeeded() {

		boolean enabled = false;

		BookDocument activeBookDocument = getActionFocusSelector().getActiveBookDocument();
		if (activeBookDocument != null) {
			enabled = true;
		}

		setEnabled(enabled);

	}

	public AtomTableDialog getAtomTableDialog() {
		return atomTableDialog;
	}

	public void setAtomTableDialog(AtomTableDialog atomTableDialog) {
		this.atomTableDialog = atomTableDialog;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setEnabledAsNeeded();
	}

}
