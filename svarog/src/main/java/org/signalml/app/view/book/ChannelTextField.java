/* ChannelTextField.java created 2008-03-05
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

import org.signalml.domain.book.StandardBook;

/** ChannelTextField
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelTextField extends JTextField implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private BookView bookView;

	public ChannelTextField() {
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

	public ChannelTextField(BookView bookView) {
		this();
		setBookView(bookView);
	}

	public BookView getBookView() {
		return bookView;
	}

	public void setBookView(BookView bookView) {
		if (this.bookView != bookView) {
			if (this.bookView != null) {
				this.bookView.removePropertyChangeListener(BookView.CURRENT_CHANNEL_PROPERTY, this);
			}
			this.bookView = bookView;
			if (bookView != null) {
				bookView.addPropertyChangeListener(BookView.CURRENT_CHANNEL_PROPERTY, this);
			}
			setCurrentText();
		}
	}

	public void setCurrentText() {
		if (bookView == null) {
			setText("");
			setEditable(false);
		} else {
			StandardBook book = bookView.getDocument().getBook();
			int currentChannel = bookView.getCurrentChannel();
			String label = book.getChannelLabel(currentChannel);
			setText((currentChannel+1) + " / " + book.getChannelCount() + (label != null ? " (" + label + ")" : ""));
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

		int count = bookView.getChannelCount();

		if (index != null) {
			// this was a number

			if (index < 1) {
				index = 1;
			}
			else if (index > count) {
				index = count;
			}

			bookView.setCurrentChannel(index-1);

		} else {
			// maybe a label ?

			StandardBook book = bookView.getDocument().getBook();
			String label;
			for (int i=0; i<count; i++) {
				label = book.getChannelLabel(i);
				if (label != null && text.equalsIgnoreCase(label)) {
					bookView.setCurrentChannel(i);
				}
			}

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
