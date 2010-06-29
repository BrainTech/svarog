/* AbstractDocument.java created 2007-09-10
 *
 */
package org.signalml.app.document;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.signalml.exception.SignalMLException;

/** AbstractDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMutableFileDocument extends AbstractFileDocument implements MutableDocument {

	public static final String SAVED_PROPERTY = "saved";

	protected boolean saved = true;

	public AbstractMutableFileDocument() throws SignalMLException {
		super();
		newDocument();
	}

	public AbstractMutableFileDocument(File file) throws SignalMLException, IOException {
		super(file);
	}

	@Override
	public boolean isSaved() {
		return saved;
	}

	@Override
	public void setSaved(boolean saved) {
		if (this.saved != saved) {
			this.saved = saved;
			pcSupport.firePropertyChange(SAVED_PROPERTY, !saved, saved);
		}
	}

	public void invalidate() {
		setSaved(false);
	}

	@Override
	public final void openDocument() throws SignalMLException, IOException {

		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}

		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(backingFile));
			readDocument(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}

		setSaved(true);

	}

	@Override
	public final void saveDocument() throws SignalMLException, IOException {

		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}

		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(backingFile,false));
			writeDocument(os);
		} finally {
			if (os != null) {
				os.close();
			}
		}

		setSaved(true);

	}

	protected abstract void readDocument(InputStream is) throws SignalMLException, IOException;

	protected abstract void writeDocument(OutputStream os) throws SignalMLException, IOException;

}
