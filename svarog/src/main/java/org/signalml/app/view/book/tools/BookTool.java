/* BookTool.java created 2008-02-23
 *
 */

package org.signalml.app.view.book.tools;

import java.awt.Cursor;
import javax.swing.event.MouseInputAdapter;
import org.signalml.app.view.book.BookView;

/** BookTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class BookTool extends MouseInputAdapter {

	protected BookView bookView;
	protected boolean engaged = false;

	public BookTool(BookView bookView) {
		super();
		this.bookView = bookView;
	}

	public BookView getSignalView() {
		return bookView;
	}

	public abstract Cursor getDefaultCursor();

	public boolean isEngaged() {
		return engaged;
	}

	public boolean isBookSelectionTool() {
		return (this instanceof SelectAtomBookTool);
	}

}
