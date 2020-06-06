/* SegmentTextField.java created 2008-03-05
 *
 */

package org.signalml.app.view.book;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextField;

/** SegmentTextField
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SegmentTextField extends JTextField implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private BookView bookView;

	public SegmentTextField() {
		super();
		setHorizontalAlignment(JTextField.CENTER);

		addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				trySet(getText());
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				setText("");
				requestFocusInWindow();
			}

		});

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				trySet(getText());
			}

		});

	}

	public SegmentTextField(BookView bookView) {
		this();
		setBookView(bookView);
	}

	public BookView getBookView() {
		return bookView;
	}

	public void setBookView(BookView bookView) {
		if (this.bookView != bookView) {
			if (this.bookView != null) {
				this.bookView.removePropertyChangeListener(BookView.CURRENT_SEGMENT_PROPERTY, this);
			}
			this.bookView = bookView;
			if (bookView != null) {
				bookView.addPropertyChangeListener(BookView.CURRENT_SEGMENT_PROPERTY, this);
			}
			setCurrentText();
		}
	}

	public void setCurrentText() {
		if (bookView == null) {
			setText("");
			setEditable(false);
		} else {
			setText((bookView.getCurrentSegment()+1) + " / " + bookView.getSegmentCount());
			setEditable(true);
		}
	}

	private void trySet(String text) {

		if (bookView == null) {
			return;
		}

		text = text.trim();

		Integer index = null;
		try {
			index = new Integer(text);
		} catch (NumberFormatException ex) {
			// proceed
		}

		if (index != null) {
			// this was a number

			int count = bookView.getDocument().getSegmentCount();

			if (index < 1) {
				index = 1;
			}
			else if (index > count) {
				index = count;
			}

			bookView.setCurrentSegment(index-1);

		}

		setCurrentText();

	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		setMinimumSize(preferredSize);
		setMaximumSize(preferredSize);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setCurrentText();
	}

}
