/* IterableMethod.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import org.signalml.method.Method;

/** IterableMethod
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface IterableMethod extends Method {

	IterableParameter[] getIterableParameters(Object data);

	Object digestIterationResult(int iteration, Object result);

}
