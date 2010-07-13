/* SuspendableMethod.java created 2007-10-18
 *
 */

package org.signalml.method;

/**
 *  This interface is to be implemented by those {@link Method methods} which support having
 *  computation suspended and later resumed. Implementing classes must provide one method that
 *  checks if the given data object represents suspended computation state, or not. Additionally,
 *  a suspendable method's {@link Method#compute(Object, org.signalml.task.Task)} method must check
 *  for REQUESTING_SUSPEND task status and be able to pick up suspended execution on data containing
 *  execution state.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SuspendableMethod {

	/**
         *  Returns true if the given data object contains suspended execution state that
	 *  is to be resumed, rather than fresh computation input data.
	 *
	 * @param data the data object
	 * @return true for suspended data, false otherwise
	 */
	boolean isDataSuspended(Object data);

}
