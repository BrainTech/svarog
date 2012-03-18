/* OpenDocumentDescriptor.java created 2007-09-17
 *
 */

package org.signalml.app.model.document;

import java.io.File;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.book.OpenBookDescriptor;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/** OpenDocumentDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentDescriptor {

	private File file;
	private ManagedDocumentType type;
	private boolean makeActive = true;

	/**
	 * Descriptor determining a signal to be opened.
	 */
	private AbstractOpenSignalDescriptor openSignalDescriptor = new RawSignalDescriptor();
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

	/**
	 * Returns a descriptor describing the signal to be opened
	 * (from a file or monitor)
	 * @return descriptor of the signal to be opened
	 */
	public AbstractOpenSignalDescriptor getOpenSignalDescriptor() {
		return openSignalDescriptor;
	}
	
	public void setOpenSignalDescriptor(AbstractOpenSignalDescriptor openSignalDescriptor) {
		this.openSignalDescriptor = openSignalDescriptor;
	}

	public OpenTagDescriptor getTagOptions() {
		return tagOptions;
	}

	public OpenBookDescriptor getBookOptions() {
		return bookOptions;
	}

}
