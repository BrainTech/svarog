/* DirectoryFileFilter.java created 2007-09-14
 *
 */
package org.signalml.app.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/** DirectoryFileFilter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DirectoryFileFilter extends FileFilter {

	private String description;

	public DirectoryFileFilter(String description) {
		super();
		this.description = description;
	}

	@Override
	public boolean accept(File f) {
		return !f.exists() || f.isDirectory();
	}

	@Override
	public String getDescription() {
		return description;
	}

}
