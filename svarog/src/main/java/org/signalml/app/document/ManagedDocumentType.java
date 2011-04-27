/* ManagedDocumentType.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.Document;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * The types of {@link Document documents} that can be managed by Svarog.
 * The description of a single type includes:
 * <ul>
 * <li>the name of this type,</li>
 * <li>the class of the document of this type,</li>
 * <li>the code for a file filter</li>
 * <li>the extensions of the files for this type of the document,</li>
 * <li>the icon of this type.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum ManagedDocumentType implements MessageSourceResolvable {

	/**
	 * type for a {@link SignalDocument}
	 */
	SIGNAL(
	        "signal",
	        SignalDocument.class,
	        "filechooser.filter.commonSignalFiles",
	new String[] { "d", "edf", "raw", "bin" },
	"org/signalml/app/icon/signal.png"
	),

	/**
	 * type for a {@link MonitorSignalDocument}
	 */
	MONITOR( 
			"monitor", 
			MonitorSignalDocument.class 
	),
	
	/**
	 * type for a {@link BookDocument}
	 */
	BOOK( 
			"book", 
			BookDocument.class, 
			"filechooser.filter.bookFiles", 
			new String[] { "b" },
			"org/signalml/app/icon/book.png" 
	),

	/**
	 * type for a {@link TagDocument}
	 */
	TAG(
	        "tag",
	        TagDocument.class,
	        "filechooser.filter.tagFiles",
	new String[] { "xml", "tag" },
	"org/signalml/app/icon/tag.png"
	);

	/**
	 * the name of this type
	 */
	private String name;
	
	/**
	 * the class that is extended/implemented by all {@link Document documents}
	 * of this type
	 */
	private Class<?> baseClass;
	
	/**
	 * the codes for a file filter
	 */
	private String[] fileFilterCodes = new String[0];
	
	/**
	 * the extensions of the files for this type of the document
	 */
	private String[][] fileFilterExtensions = new String[0][0];
	/**
	 * the icon of this type
	 */
	private Icon icon;

	/**
	 * Constructor. Sets only a name and a class.
	 * @param name the name of the type
	 * @param baseClass the class that is extended/implemented by all
	 * {@link Document documents} of the created type
	 */
	private ManagedDocumentType(String name, Class<?> baseClass) {
		this.name = name;
		this.baseClass = baseClass;
	}

	/**
	 * Constructor. Sets all parameters of this type.
	 * @param name the name of this type
	 * @param baseClass the class that is extended/implemented by all
	 * {@link Document documents} of this type
	 * @param fileFilterCode the code for a file filter
	 * @param fileFilterExtensions the extensions of the files for this type of the document
	 * @param iconPath the path to the icon of this type
	 */
	private ManagedDocumentType(String name, Class<?> baseClass, String fileFilterCode, String[] fileFilterExtensions, String iconPath) {
		this.name = name;
		this.baseClass = baseClass;
		this.fileFilterCodes = new String[] { fileFilterCode };
		this.fileFilterExtensions = new String[][] { fileFilterExtensions };
		if (iconPath != null) {
			icon = IconUtils.loadClassPathIcon(iconPath);
		}
	}

	/**
	 * Returns the name of this type.
	 * @return the name of this type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the class that is extended/implemented by all
	 * {@link Document documents} of this type
	 * @return the class that is extended/implemented by all
	 * documents of this type
	 */
	public Class<?> getBaseClass() {
		return baseClass;
	}

	/**
	 * Returns the codes for a file filter.
	 * @return the codes for a file filter
	 */
	public String[] getFileFilterCodes() {
		return fileFilterCodes;
	}

	/**
	 * Returns the extensions of the files for this type of the
	 * {@link Document document}.
	 * @return the extensions of the files for this type of the document
	 */
	public String[][] getFileFilterExtensions() {
		return fileFilterExtensions;
	}

	/**
	 * Returns the icon of this type.
	 * @return the icon of this type
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Returns the filters of the files basing on the
	 * {@link #getFileFilterExtensions() extensions} of the files.
	 * @param messageSource the source of messages (labels) in Svarog
	 * @return the created filters
	 */
	public FileFilter[] getFileFilters(MessageSourceAccessor messageSource) {
		int len = Math.min(fileFilterCodes.length, fileFilterExtensions.length);
		FileFilter[] filters = new FileFilter[len];
		for (int i=0; i<len; i++) {
			filters[i] = new FileNameExtensionFilter(messageSource.getMessage(fileFilterCodes[i]), fileFilterExtensions[i]);
		}
		return filters;
	}

	/**
	 * Returns all possible types.
	 * @return all possible types
	 */
	public static ManagedDocumentType[] getAll() {
		return new ManagedDocumentType[] { SIGNAL, MONITOR, BOOK, TAG };
	}

	/**
	 * Returns the type based on the class of a {@link Document document}
	 * of this type.
	 * @param clazz the class of a document
	 * @return the found type
	 */
	public static ManagedDocumentType getForClass(Class<?> clazz) {
		if (clazz == MonitorSignalDocument.class) {
			return ManagedDocumentType.MONITOR;
		}
		ManagedDocumentType[] all = getAll();
		for (int i=0; i<all.length; i++) {
			if (all[i].baseClass.isAssignableFrom(clazz)) {
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
