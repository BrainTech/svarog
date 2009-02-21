/* MutableDocument.java created 2007-09-20
 * 
 */

package org.signalml.app.document;

import java.io.IOException;

import org.signalml.exception.SignalMLException;

/** MutableDocument
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MutableDocument extends Document {

	boolean isSaved();
	void setSaved(boolean saved);

	void newDocument() throws SignalMLException;
	void saveDocument() throws SignalMLException, IOException;
		
}
