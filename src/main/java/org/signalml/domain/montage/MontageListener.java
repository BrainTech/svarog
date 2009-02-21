/* MontageEventListener.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/** MontageEventListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageListener extends EventListener {

	void montageStructureChanged( MontageEvent ev );
	
	void montageChannelsAdded( MontageEvent ev );
	
	void montageChannelsRemoved( MontageEvent ev );
	
	void montageChannelsChanged( MontageEvent ev );
	
	void montageReferenceChanged( MontageEvent ev );
	
}
