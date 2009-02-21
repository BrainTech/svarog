/* AbstractFileDocument.java created 2008-03-04
 * 
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.MessageSourceResolvable;

/** AbstractFileDocument
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFileDocument extends AbstractDocument implements FileBackedDocument, MessageSourceResolvable, PropertyProvider {

	public static final String BACKING_FILE_PROPERTY = "backingFile";	

	protected File backingFile = null;

	protected AbstractFileDocument() {
		super();
	}

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
		if( !Util.equalsWithNulls( this.backingFile, backingFile ) ) {
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
		if( backingFile != null ) {
			return backingFile.getAbsolutePath();
		} else {
			return getClass().getSimpleName();
		}
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();
		
		list.add( new LabelledPropertyDescriptor("property.document.backingFile", "backingFile", AbstractFileDocument.class) );
		
		return list;
		
	}
	
}
