/* MP5ExecutorConfigurer.java created 2008-02-18
 * 
 */

package org.signalml.method.mp5;

import org.signalml.method.ComputationException;

/** MP5ExecutorConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MP5ExecutorConfigurer {

	void configure( MP5Executor executor ) throws ComputationException;
	
}
