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

import org.signalml.plugin.export.SignalMLException;

/**
 * Abstract implementation of two interfaces: {@link FileBackedDocument} and
 * {@link MutableDocument}.
 * Apart from what was already implemented in {@link AbstractFileDocument}
 * allows to:
 * <ul>
 * <li>save this document to the backing file,</li>
 * <li>open this document from the backing file,</li>
 * <li>get and set if this document is saved.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMutableFileDocument extends AbstractFileDocument implements MutableDocument {

	public static final String SAVED_PROPERTY = "saved";

	/**
	 * true if the document is saved, false if some information in this
	 * document are not saved
	 */
	protected boolean saved = true;

	/**
	 * Constructor. {@link MutableDocument#newDocument() Initializes}
	 * this document.
	 * @throws SignalMLException never thrown in implementations
	 */
	public AbstractMutableFileDocument() throws SignalMLException {
		super();
		newDocument();
	}

	/**
	 * Constructor. Sets the file that backs this document and
	 * {@link #openDocument() opens} this document
	 * @param file the file that backs this document
	 * @throws SignalMLException if there is no backing file or
	 * the document stored in the file has invalid format or other
	 * non I/O error occurs while reading a file
	 * @throws IOException if I/O error occurs while reading the file
	 */
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

	/**
	 * Sets that the document is not saved.
	 */
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

	/**
	 * Reads the data of this document from a given input stream.
	 * @param is the input stream from which the file is to be read
	 * @throws SignalMLException TODO exceptions never thrown in implementation
	 * @throws IOException
	 */
	protected abstract void readDocument(InputStream is) throws SignalMLException, IOException;

	/**
	 * Writes this document to the given output stream.
	 * @param os the output stream to which this document is to be written
	 * @throws SignalMLException TODO exceptions never thrown in implementation
	 * @throws IOException
	 */
	protected abstract void writeDocument(OutputStream os) throws SignalMLException, IOException;

}
