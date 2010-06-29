/* InitializingMethodResultConsumer.java created 2007-10-23
 *
 */

package org.signalml.app.method;

/** InitializingMethodResultConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface InitializingMethodResultConsumer extends MethodResultConsumer {

	void initialize(ApplicationMethodManager manager);

}
