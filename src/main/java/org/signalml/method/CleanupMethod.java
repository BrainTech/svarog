/* CleanupMethod.java created 2008-02-15
 *
 */

package org.signalml.method;

/**
 *  CleanupMethod interface is to be implemented by those {@link Method methods} which need to clean
 *  some resources after use.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface CleanupMethod {

	/**
	 * Cleans some resources used as arguments of this method.
	 * @param data Data to be cleaned
	 */
	void cleanUp(Object data);

}
