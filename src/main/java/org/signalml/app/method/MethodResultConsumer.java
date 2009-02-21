/* MethodResultConsumer.java created 2007-10-22
 * 
 */

package org.signalml.app.method;

import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;

/** MethodResultConsumer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MethodResultConsumer {

	boolean consumeResult( Method method, Object methodData, Object methodResult ) throws SignalMLException;
	
}
