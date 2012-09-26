/* AbstractFileDocument.java created 2008-03-04
 *
 */

package org.signalml.app.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.AbstractDocument;
import org.signalml.plugin.export.signal.Document;
import org.signalml.util.Util;
import org.springframework.context.MessageSourceResolvable;

/**
 * Abstract implementation of a {@link Document document} with a
 * {@link FileBackedDocument backing file}.
 * Contains this file and as a {@link #getDefaultMessage() default message}
 * uses the path to it.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFileDocument extends AbstractDocument implements FileBackedDocument, MessageSourceResolvable, PropertyProvider {

	public static final String BACKING_FILE_PROPERTY = "backingFile";

	/**
	 * the file with which this document is backed
	 */
	protected File backingFile = null;

	/**
	 * Constructor.
	 */
	protected AbstractFileDocument() {
		super();
	}

	/**
	 * Constructor. Sets the file that backs this document and
	 * {@link #openDocument() opens} this document
	 * @param file the file from which this document will be read
	 * @throws SignalMLException if the file is null or
	 * the document stored in the file has invalid format or other
	 * non I/O error occurs while reading a file
	 * @throws IOException if I/O error occurs while reading the file
	 */
	public AbstractFileDocument(File file) throws SignalMLException, IOException {
		this.backingFile = file;
		openDocument();
	}

	@Override
	public File getBackingFile() {
		return backingFile;
	}

	@Override
	public void setBackingFile(File backingFile) {
		if (!Util.equalsWithNulls(this.backingFile, backingFile)) {
			File oldFile = this.backingFile;
			this.backingFile = backingFile;
			pcSupport.firePropertyChange(BACKING_FILE_PROPERTY, oldFile, backingFile);
		}
	}

	@Override
	public String[] getCodes() {
		return new String[] { "document" };
	}

	@Override
	public Object[] getArguments() {
		return new Object[] {
				   backingFile != null ? backingFile.getAbsolutePath() : "?"
			   };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

	@Override
	public String toString() {
		if (backingFile != null) {
			return backingFile.getAbsolutePath();
		} else {
			return getClass().getSimpleName();
		}
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(_("backing file"), "backingFile", AbstractFileDocument.class));

		return list;

	}

}
