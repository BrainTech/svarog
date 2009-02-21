/* AnyChangeDocumentAdapter.java created 2007-11-13
 * 
 */

package org.signalml.app.view.element;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** AnyChangeDocumentAdapter
 *
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
	
	public abstract void anyUpdate(DocumentEvent e);

}
