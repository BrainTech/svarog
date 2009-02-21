/* EventProducerBook.java created 2008-02-28
 * 
 */

package org.signalml.domain.book;

import java.beans.PropertyChangeListener;

/** EventProducerBook
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface EventProducerBook {

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
	
	void addBookListener(BookListener listener);
	
	void removeBookListener(BookListener listener);
	
}
