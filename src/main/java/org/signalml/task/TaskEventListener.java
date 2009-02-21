/* TaskEventListener.java created 2007-09-12
 * 
 */
package org.signalml.task;

import java.util.EventListener;

/** A listener listening for {@link TaskEvent TaskEvents}
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TaskEventListener extends EventListener {
	
	void taskStarted( TaskEvent ev );
	
	void taskSuspended( TaskEvent ev );
	
	void taskResumed( TaskEvent ev );
	
	void taskAborted( TaskEvent ev );
	
	void taskFinished( TaskEvent ev );
	
	void taskTickerUpdated( TaskEvent ev );
	
	void taskMessageSet( TaskEvent ev );

	void taskRequestChanged( TaskEvent ev );
	
}
