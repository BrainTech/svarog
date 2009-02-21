/* MP5Executor.java created 2008-02-08
 * 
 */

package org.signalml.method.mp5;

import java.io.File;

import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.springframework.context.MessageSourceResolvable;

/** MP5Executor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MP5Executor extends MessageSourceResolvable {

	String getUID();
	
	boolean execute( MP5Data data, int segment, File resultFile, MethodExecutionTracker tracker ) throws ComputationException;
		
}
