/* MP5ExecutorManagerListener.java created 2008-02-08
 * 
 */

package org.signalml.app.method.mp5;

import java.util.EventListener;

/** MP5ExecutorManagerListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MP5ExecutorManagerListener extends EventListener {

	void executorAdded( MP5ExecutorManagerEvent ev );

	void executorChanged( MP5ExecutorManagerEvent ev );
	
	void executorRemoved( MP5ExecutorManagerEvent ev );

	void defaultExecutorChanged( MP5ExecutorManagerEvent ev );
	
}
