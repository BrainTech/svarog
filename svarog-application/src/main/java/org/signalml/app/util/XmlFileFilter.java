/**
 * 
 */
package org.signalml.app.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author oskar
 * 
 */
public class XmlFileFilter implements FileFilter {

	private final String[] okFileExtensions = new String[] { "xml" };

	public boolean accept(File file) {
		for (String extension : okFileExtensions) {
			if (file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
}
