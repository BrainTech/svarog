/* SerializableMethod.java created 2008-02-15
 *
 */

package org.signalml.method;

import java.io.File;
import java.io.IOException;

import org.signalml.plugin.export.SignalMLException;

/**
 * SerializableMethod interface is to be implemented by those {@link Method methods} which need to
 * be saved to and loaded from file.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SerializableMethod extends SuspendableMethod {

	/**
	 * Writes data to working directory.
	 *
	 * @param data Data to save
	 * @return file with saved Data
	 * @throws IOException when directory does not exist
	 * @throws SignalMLException when working directory of data is null
	 */
	public File writeToPersistence(Object data) throws IOException, SignalMLException;

	/**
	 * Reads data from specified file to specified Data object.
	 *
	 * @param data to save loaded Data to
	 * @param file to read from
	 * @throws IOException when file does not exist
	 * @throws SignalMLException when any SignalMLException occured (e.g. MissingCodecException)
	 */
	public void readFromPersistence(Object data, File file) throws IOException, SignalMLException;

}
