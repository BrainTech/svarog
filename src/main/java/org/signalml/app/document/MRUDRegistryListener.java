/* MRUDRegistryListener.java created 2007-09-21
 *
 */

package org.signalml.app.document;

import java.util.EventListener;

/** MRUDRegistryListener
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MRUDRegistryListener extends EventListener {

	void mrudEntryRegistered(MRUDRegistryEvent e);

	void mrudEntryRemoved(MRUDRegistryEvent e);

}
