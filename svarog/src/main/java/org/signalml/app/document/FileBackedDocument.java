/* SingleFileBackedDocument.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.io.File;
import org.signalml.plugin.export.signal.Document;

/**
 * Interface for a {@link Document} which is backed with a file.
 * Allows to get and set this file.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface FileBackedDocument extends Document {

	/**
	 * Returns the file with which this document is backed.
	 * @return the file with which this document is backed
	 */
	File getBackingFile();

	/**
	 * Sets the file with which this document is backed.
	 * @param file the file with which this document is backed
	 */
	void setBackingFile(File file);

}
