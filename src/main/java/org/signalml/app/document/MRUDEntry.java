/* MRUDEntry.java created 2007-09-12
 *
 */
package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/** MRUDEntry
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mrud")
public class MRUDEntry implements MessageSourceResolvable, PropertyProvider {

	private static final long serialVersionUID = 1L;

	private String path;
	private Date lastTimeOpened;
	private ManagedDocumentType documentType;
	private Class<?> documentClass;

	@XStreamOmitField
	private transient File file;

	protected MRUDEntry() {
	}

	public MRUDEntry(ManagedDocumentType documentType, Class<?> documentClass, String path) {
		this.file = (new File(path)).getAbsoluteFile();
		this.path = this.file.getAbsolutePath();
		this.documentType = documentType;
		this.documentClass = documentClass;
	}

	public String getPath() {
		return path;
	}

	public File getFile() {
		if (file == null) {
			file = new File(path);
		}
		return file;
	}

	public String getFileName() {
		return getFile().getName();
	}

	public Date getLastTimeOpened() {
		return lastTimeOpened;
	}

	public void setLastTimeOpened(Date lastTimeOpen) {
		this.lastTimeOpened = lastTimeOpen;
	}

	public ManagedDocumentType getDocumentType() {
		return documentType;
	}

	public Class<?> getDocumentClass() {
		return documentClass;
	}

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
