/* InitializingMethod.java created 2007-10-18
 *
 */

package org.signalml.method;

import org.signalml.exception.SignalMLException;

/**
 *  This interface is to be implemented by those {@link Method methods} which need to perform
 *  initialization prior to first use.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface InitializingMethod {

	/**
         * Initializes the method singleton.
	 * @throws SignalMLException on errors
	 */
	void initialize() throws SignalMLException;

}
