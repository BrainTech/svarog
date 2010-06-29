/* TagStyleListener.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventListener;

/** TagStyleListener
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TagStyleListener extends EventListener {

	void tagStyleAdded(TagStyleEvent e);

	void tagStyleRemoved(TagStyleEvent e);

	void tagStyleChanged(TagStyleEvent e);

}
