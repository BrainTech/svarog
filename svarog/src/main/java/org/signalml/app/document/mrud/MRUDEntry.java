/* MRUDEntry.java created 2007-09-12
 *
 */
package org.signalml.app.document.mrud;

import java.beans.IntrospectionException;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Contains the serializable description of a file:
 * <ul>
 * <li>path,</li>
 * <li>last time when the document was open,</li>
 * <li>the type of a document that can be created using the described file,</li>
 * <li>the class of the document</li>
 * </ul>
 * and the described file, which is not serialized.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mrud")
public class MRUDEntry implements MessageSourceResolvable, PropertyProvider {

	private static final long serialVersionUID = 1L;

	/**
	 * the path to the file
	 */
	private String path;
	/**
	 * the last time when the file was opened
	 */
	private Date lastTimeOpened;

	/**
	 * the {@link ManagedDocumentType type} of a document that can be created
	 * using the described file
	 */
	private ManagedDocumentType documentType;

	/**
	 * the class of the document TODO only set and never read?
	 */
	private Class<?> documentClass;

	/**
	 * the described file
	 */
	@XStreamOmitField
	private transient File file;

	/**
	 * Empty constructor.
	 */
	protected MRUDEntry() {
	}

	/**
	 * Constructor. Sets:
	 * <ul>
	 * <li>the type of {@link ManagedDocumentType type} of the document,</li>
	 * <li>the class of the document,</li>
	 * <li>the new file created on the basis of the provided path,</li>
	 * <li>the path to the file converted to the absolute path.</li>
	 * </ul>
	 * @param documentType the type of type of the document
	 * @param documentClass the class of the document
	 * @param path the path to the file
	 */
	public MRUDEntry(ManagedDocumentType documentType, Class<?> documentClass, String path) {
		this.file = (new File(path)).getAbsoluteFile();
		this.path = this.file.getAbsolutePath();
		this.documentType = documentType;
		this.documentClass = documentClass;
	}

	/**
	 * Returns the absolute path to the described file.
	 * @return the absolute path to the described file
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns the described file.
	 * If it doesn't exist, it is created
	 * @return the described file
	 */
	public File getFile() {
		if (file == null) {
			file = new File(path);
		}
		return file;
	}

	/**
	 * Returns the name of the described file.
	 * @return the name of the described file
	 */
	public String getFileName() {
		return getFile().getName();
	}

	/**
	 * Returns the last time when the file was opened.
	 * @return the last time when the file was opened
	 */
	public Date getLastTimeOpened() {
		return lastTimeOpened;
	}

	/**
	 * Sets the last time when the file was opened.
	 * @param lastTimeOpen the last time when the file was opened
	 */
	public void setLastTimeOpened(Date lastTimeOpen) {
		this.lastTimeOpened = lastTimeOpen;
	}

	/**
	 * Returns the {@link ManagedDocumentType type} of a document that can
	 * be created using the described file.
	 * @return the type of a document
	 */
	public ManagedDocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * Returns the class of the document that can be created using
	 * the described file.
	 * @return the class of the document
	 */
	public Class<?> getDocumentClass() {
		return documentClass;
	}

	/**
	 * Returns the path to the described file.
	 */
	@Override
	public String toString() {
		return path;
	}

	@Override
	public String[] getCodes() {
		return new String[0];
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String getDefaultMessage() {
		return getFileName();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.mrud.path", "path", MRUDEntry.class, "getPath", null));
		list.add(new LabelledPropertyDescriptor("property.mrud.documentType", "documentType", MRUDEntry.class, "getDocumentType", null));
		list.add(new LabelledPropertyDescriptor("property.mrud.lastTimeOpened", "lastTimeOpened", MRUDEntry.class));

		return list;

	}

}
