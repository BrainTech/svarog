/* SingleFileBackedDocument.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.io.File;

import org.signalml.plugin.export.signal.Document;

/** SingleFileBackedDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface FileBackedDocument extends Document {

	File getBackingFile();
	void setBackingFile(File file);

}
