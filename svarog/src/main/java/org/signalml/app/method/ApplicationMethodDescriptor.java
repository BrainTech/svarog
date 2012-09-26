/* ApplicationMethodDescriptor.java created 2007-10-22
 *
 */

package org.signalml.app.method;

import org.signalml.method.Method;
import org.signalml.plugin.export.method.BaseMethodData;

/** ApplicationMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ApplicationMethodDescriptor {

	Method getMethod();

	String getName();
	String getIconPath();

	MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly);

	MethodConfigurer getConfigurer(ApplicationMethodManager methodManager);
	MethodResultConsumer getConsumer(ApplicationMethodManager methodManager);

	BaseMethodData createData(ApplicationMethodManager methodManager);

}
