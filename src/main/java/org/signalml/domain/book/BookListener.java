/* BookListener.java created 2008-02-23
 * 
 */

package org.signalml.domain.book;

import java.util.EventListener;

/** BookListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface BookListener extends EventListener {

	void bookStructureChanged( BookEvent ev );
	
	void segmentAdded( BookEvent ev );
	
	void segmentChanged( BookEvent ev );

	void segmentRemoved( BookEvent ev );

	void segmentAtomsChanged( BookEvent ev );
	
	void atomAdded( BookEvent ev );

	void atomChanged( BookEvent ev );
	
	void atomRemoved( BookEvent ev );
	
}
