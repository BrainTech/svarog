/* NewArtifactResult.java created 2007-11-01
 *
 */

package org.signalml.plugin.newartifact.data;

import java.io.File;
import java.io.Serializable;

import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;

/** NewArtifactResult
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactResult extends PluginComputationMgrStepResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private File tagFile;

	public File getTagFile() {
		return tagFile;
	}

	public void setTagFile(File tagFile) {
		this.tagFile = tagFile;
	}

}
