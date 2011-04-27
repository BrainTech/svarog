/* ApplicationMethodDescriptor.java created 2007-10-22
 *
 */

package org.signalml.app.method;

import org.signalml.method.Method;

/** ApplicationMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ApplicationMethodDescriptor {

	Method getMethod();

	String getNameCode();
	String getIconPath();

	MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly);

	MethodConfigurer getConfigurer(ApplicationMethodManager methodManager);
	MethodResultConsumer getConsumer(ApplicationMethodManager methodManager);

	Object createData(ApplicationMethodManager methodManager);

}
