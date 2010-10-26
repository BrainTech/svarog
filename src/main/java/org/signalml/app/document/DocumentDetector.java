/* DocumentDetector.java created 2007-09-18
 *
 */

package org.signalml.app.document;

import java.io.File;
import java.io.IOException;

/**
 * This interface allows to detect the {@link ManagedDocumentType type} of
 * a document stored in the file.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentDetector {

	/**
	 * Detects the {@link ManagedDocumentType type} of a document stored in the
	 * given file.
	 * @param file the file with the document
	 * @return the type of the document
	 * @throws IOException if I/O error occurs
	 */
	ManagedDocumentType detectDocumentType(File file) throws IOException;

}
