/* BookToolForwardingMouseAdapter.java created 2008-02-23
 *
 */

package org.signalml.app.view.book;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

/** BookToolForwardingMouseAdapter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToolForwardingMouseAdapter extends MouseAdapter {

	private SelectAtomBookTool selectAtomBookTool;
	private BookTool bookTool;

	public BookToolForwardingMouseAdapter() {
	}

	public BookTool getBookTool() {
		return bookTool;
	}

	public void setBookTool(BookTool bookTool) {
		this.bookTool = bookTool;
	}

	public SelectAtomBookTool getSelectAtomBookTool() {
		return selectAtomBookTool;
	}

	public void setSelectAtomBookTool(SelectAtomBookTool selectAtomBookTool) {
		this.selectAtomBookTool = selectAtomBookTool;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!bookTool.isBookSelectionTool() && (SwingUtilities.isMiddleMouseButton(e) || (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()))) {
			selectAtomBookTool.mousePressed(e);
		}
		else {
			if (bookTool != null) {
				bookTool.mousePressed(e);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (bookTool != null) {
			bookTool.mouseReleased(e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (bookTool != null) {
			bookTool.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (bookTool != null) {
			bookTool.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (bookTool != null) {
			bookTool.mouseExited(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (bookTool != null) {
			bookTool.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (bookTool != null) {
			bookTool.mouseMoved(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (bookTool != null) {
			bookTool.mouseWheelMoved(e);
		}
	}

}
