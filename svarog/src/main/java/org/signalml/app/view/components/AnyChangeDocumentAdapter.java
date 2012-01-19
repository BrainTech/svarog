/* AnyChangeDocumentAdapter.java created 2007-11-13
 *
 */

package org.signalml.app.view.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Abstract DocumentListener which calls {@link #anyUpdate(DocumentEvent)}
 * function on every change.
 * This function must be implemented in the sub-class.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AnyChangeDocumentAdapter implements DocumentListener {

	@Override
	public void changedUpdate(DocumentEvent e) {
		anyUpdate(e);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		anyUpdate(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		anyUpdate(e);
	}

	/**
	 * Function called when any change associated with the DocumentListener
	 * is performed.
	 * This function must be implemented in the sub-class.
	 * @param e the document event
	 */
	public abstract void anyUpdate(DocumentEvent e);

}
