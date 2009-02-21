/* ManagedDocumentType.java created 2007-09-20
 * 
 */

package org.signalml.app.document;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.signalml.app.util.IconUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** ManagedDocumentType
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum ManagedDocumentType implements MessageSourceResolvable {

	SIGNAL( 
			"signal", 
			SignalDocument.class, 
			"filechooser.filter.commonSignalFiles", 
			new String[] { "d", "edf", "raw", "bin" }, 
			"org/signalml/app/icon/signal.png" 
	),
	
	BOOK( 
			"book", 
			BookDocument.class, 
			"filechooser.filter.bookFiles", 
			new String[] { "b" },
			"org/signalml/app/icon/book.png" 
	),
	
	TAG( 
			"tag", 
			TagDocument.class, 
			"filechooser.filter.tagFiles", 
			new String[] { "xml", "tag" },
			"org/signalml/app/icon/tag.png" 
	);
	
	private String name;
	private Class<?> baseClass;
	private String[] fileFilterCodes = new String[0];
	private String[][] fileFilterExtensions = new String[0][0];
	private Icon icon;
			
	private ManagedDocumentType(String name, Class<?> baseClass) {
		this.name = name;
		this.baseClass = baseClass;
	}

	private ManagedDocumentType(String name, Class<?> baseClass, String fileFilterCode, String[] fileFilterExtensions, String iconPath) {
		this.name = name;
		this.baseClass = baseClass;
		this.fileFilterCodes = new String[] { fileFilterCode };
		this.fileFilterExtensions = new String[][] { fileFilterExtensions };
		if( iconPath != null ) {
			icon = IconUtils.loadClassPathIcon(iconPath);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?> getBaseClass() {
		return baseClass;
	}	
	
	public String[] getFileFilterCodes() {
		return fileFilterCodes;
	}

	public String[][] getFileFilterExtensions() {
		return fileFilterExtensions;
	}
	
	public Icon getIcon() {
		return icon;
	}

	public FileFilter[] getFileFilters(MessageSourceAccessor messageSource) {
		int len = Math.min(fileFilterCodes.length, fileFilterExtensions.length);
		FileFilter[] filters = new FileFilter[len];
		for( int i=0; i<len; i++ ) {
			filters[i] = new FileNameExtensionFilter(messageSource.getMessage(fileFilterCodes[i]), fileFilterExtensions[i]);
		}
		return filters;
	}
	
	public static ManagedDocumentType[] getAll() {
		return new ManagedDocumentType[] { SIGNAL, BOOK, TAG };
	}
	
	public static ManagedDocumentType getForClass(Class<?> clazz) {
		ManagedDocumentType[] all = getAll();
		for( int i=0; i<all.length; i++ ) {
			if( all[i].baseClass.isAssignableFrom(clazz) ) {
				return all[i];
			}
		}
		return null;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}
	
}
