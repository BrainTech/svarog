/* ArtifactResult.java created 2007-11-01
 * 
 */

package org.signalml.method.artifact;

import java.io.File;
import java.io.Serializable;

/** ArtifactResult
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private File tagFile;

	public File getTagFile() {
		return tagFile;
	}

	public void setTagFile(File tagFile) {
		this.tagFile = tagFile;
	}
		
}
