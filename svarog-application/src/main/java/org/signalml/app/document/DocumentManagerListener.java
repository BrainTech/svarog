/* DocumentManagerListener.java created 2007-09-21
 * 
 */

package org.signalml.app.document;

import java.util.EventListener;

/** DocumentManagerListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentManagerListener extends EventListener {

	void documentAdded(DocumentManagerEvent e);

	void documentRemoved(DocumentManagerEvent e);
	
	void documentPathChanged(DocumentManagerEvent e);
	
}
