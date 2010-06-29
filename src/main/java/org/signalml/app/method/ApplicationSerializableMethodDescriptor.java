/* ApplicationSerializableMethodDescriptor.java created 2008-02-15
 *
 */

package org.signalml.app.method;

/** ApplicationSerializableMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface ApplicationSerializableMethodDescriptor {

	Object createDeserializedData(ApplicationMethodManager methodManager);

}
