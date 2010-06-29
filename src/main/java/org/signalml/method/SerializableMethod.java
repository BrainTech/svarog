/* SerializableMethod.java created 2008-02-15
 *
 */

package org.signalml.method;

import java.io.File;
import java.io.IOException;

import org.signalml.exception.SignalMLException;

/** SerializableMethod
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SerializableMethod extends SuspendableMethod {

	public File writeToPersistence(Object data) throws IOException, SignalMLException;

	public void readFromPersistence(Object data, File file) throws IOException, SignalMLException;

}
