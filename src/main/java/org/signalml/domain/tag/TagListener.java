/* TagListener.java created 2007-10-01
 * 
 */

package org.signalml.domain.tag;

import java.util.EventListener;

/** TagListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TagListener extends EventListener {

	void tagAdded(TagEvent e);

	void tagRemoved(TagEvent e);

	void tagChanged(TagEvent e);
	
}
