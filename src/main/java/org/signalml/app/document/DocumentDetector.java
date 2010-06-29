/* DocumentDetector.java created 2007-09-18
 *
 */

package org.signalml.app.document;

import java.io.File;
import java.io.IOException;

/** DocumentDetector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DocumentDetector {

	ManagedDocumentType detectDocumentType(File file) throws IOException;

}
