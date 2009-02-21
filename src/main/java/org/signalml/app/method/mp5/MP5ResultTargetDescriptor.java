/* MP5ResultTargetDescriptor.java created 2008-02-20
 * 
 */

package org.signalml.app.method.mp5;

import java.io.File;

/** MP5ResultTargetDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ResultTargetDescriptor {

	private boolean openInWindow;
	
	private boolean saveToFile;
	
	private File bookFile;

	public boolean isOpenInWindow() {
		return openInWindow;
	}

	public void setOpenInWindow(boolean openInWindow) {
		this.openInWindow = openInWindow;
	}

	public boolean isSaveToFile() {
		return saveToFile;
	}

	public void setSaveToFile(boolean saveToFile) {
		this.saveToFile = saveToFile;
	}

	public File getBookFile() {
		return bookFile;
	}

	public void setBookFile(File bookFile) {
		this.bookFile = bookFile;
	}	
	
}
