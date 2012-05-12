/* ArtifactConfiguration.java created 2008-02-08
 *
 */

package org.signalml.plugin.newartifact.data;

import org.signalml.plugin.data.method.PluginMethodWithWorkDirConfiguration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ArtifactConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */

@XStreamAlias("artifactConfiguration")
public class NewArtifactConfiguration extends PluginMethodWithWorkDirConfiguration {

	private static final long serialVersionUID = -1858366458174508013L;

	public static final String NAME = "artifactConfig";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setName(String name) {

	}

}