/* ApplcationIterableMethodDescriptor.java created 2007-12-05
 *
 */

package org.signalml.app.method;

/** ApplcationIterableMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ApplicationIterableMethodDescriptor extends ApplicationMethodDescriptor {

	String getIterationNameCode();

	String getIterationIconPath();

	MethodIterationResultConsumer getIterationConsumer(ApplicationMethodManager methodManager);

}
