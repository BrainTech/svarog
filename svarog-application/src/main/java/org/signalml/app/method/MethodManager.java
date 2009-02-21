/* MethodManager.java created 2007-10-22
 * 
 */

package org.signalml.app.method;

import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;

/** MethodManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MethodManager {

	int getMethodCount();
	Method[] getMethods();
	Method getMethodAt(int index);
	
	Method getMethodByName(String name);
	Method getMethodByUID(String uid);
	
	Method registerMethod(Class<?> clazz) throws SignalMLException;
	void registerMethod(Method method);
	
	void removeMethod(Method method);
	
}
