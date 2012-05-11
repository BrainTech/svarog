/* StagerConfiguration.java created 2008-02-08
 *
 */

package org.signalml.plugin.newstager.data;

import org.signalml.plugin.data.method.PluginMethodWithWorkDirConfiguration;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** NewStagerConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("stagerConfiguration")
public class NewStagerConfiguration extends PluginMethodWithWorkDirConfiguration {

	private static final long serialVersionUID = 3612902699666689274L;

	public static final String NAME = "stagerConfig";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

}
