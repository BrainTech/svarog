/* ArtifactConfiguration.java created 2008-02-08
 *
 */

package org.signalml.plugin.newartifact.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ArtifactConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */

@XStreamAlias("artifactConfiguration")
public class NewArtifactConfiguration implements Serializable, Preset {

	private static final long serialVersionUID = -1858366458174508013L;

	public static final String NAME = "artifactConfig";

	private String workingDirectoryPath;

	public String getWorkingDirectoryPath() {
		return workingDirectoryPath;
	}

	public void setWorkingDirectoryPath(String workingDirectoryPath) {
		this.workingDirectoryPath = workingDirectoryPath;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setName(String name) {

	}

}