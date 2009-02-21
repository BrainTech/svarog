/* InitializingMethodConfigurer.java created 2007-10-23
 * 
 */

package org.signalml.app.method;

/** InitializingMethodConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface InitializingMethodConfigurer extends MethodConfigurer {

	void initialize( ApplicationMethodManager manager );
		
}
