/* ExtensionBasedDocumentDetector.java created 2007-09-18
 * 
 */

package org.signalml.app.document;

import java.io.File;
import java.io.IOException;

import org.signalml.util.Util;

/** ExtensionBasedDocumentDetector
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExtensionBasedDocumentDetector implements DocumentDetector {

	@Override
	public ManagedDocumentType detectDocumentType(File file) throws IOException {
		String ext = Util.getFileExtension(file,false);
		if( ext == null ) {
			// assume signal for extension-less files
			return ManagedDocumentType.SIGNAL;
		}
		if( ext.equalsIgnoreCase("B") ) {
			return ManagedDocumentType.BOOK;
		}
		if( ext.equalsIgnoreCase("XML") || ext.equalsIgnoreCase("TAG") ) {
			return ManagedDocumentType.TAG;
		}
		// assume signal for all other extensions
		return ManagedDocumentType.SIGNAL;
	}
	
}
