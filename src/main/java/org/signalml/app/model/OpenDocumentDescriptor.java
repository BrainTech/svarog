/* OpenDocumentDescriptor.java created 2007-09-17
 *
 */

package org.signalml.app.model;

import java.io.File;

import org.signalml.app.document.ManagedDocumentType;

/** OpenDocumentDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentDescriptor {

	private File file;
	private ManagedDocumentType type;
	private boolean makeActive;

	private OpenSignalDescriptor signalOptions = new OpenSignalDescriptor();
	private OpenTagDescriptor tagOptions = new OpenTagDescriptor();
	private OpenBookDescriptor bookOptions = new OpenBookDescriptor();

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public ManagedDocumentType getType() {
		return type;
	}

	public void setType(ManagedDocumentType type) {
		this.type = type;
	}

	public boolean isMakeActive() {
		return makeActive;
	}

	public void setMakeActive(boolean makeActive) {
		this.makeActive = makeActive;
	}

	public OpenSignalDescriptor getSignalOptions() {
		return signalOptions;
	}

	public OpenTagDescriptor getTagOptions() {
		return tagOptions;
	}

	public OpenBookDescriptor getBookOptions() {
		return bookOptions;
	}

}
