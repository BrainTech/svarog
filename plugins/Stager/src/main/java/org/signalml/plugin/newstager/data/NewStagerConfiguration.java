/* StagerConfiguration.java created 2008-02-08
 * 
 */

package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** StagerConfiguration
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("stagerConfiguration")
public class NewStagerConfiguration implements Serializable, Preset {

	private static final long serialVersionUID = 3612902699666689274L;
	
	public static final String NAME = "stagerConfig";
	
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
		// TODO Auto-generated method stub
		
	}
	
}
