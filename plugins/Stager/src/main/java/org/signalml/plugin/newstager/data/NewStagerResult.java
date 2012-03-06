/* NewStagerResult.java created 2008-02-08
 * 
 */

package org.signalml.plugin.newstager.data;

import java.io.File;
import java.io.Serializable;

import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;

/**
 * NewStagerResult
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewStagerResult extends PluginComputationMgrStepResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private File tagFile;

	private double deltaThr;
	private double alphaThr;
	private double spindleThr;
	private double emgTone;

	public File getTagFile() {
		return tagFile;
	}

	public void setTagFile(File tagFile) {
		this.tagFile = tagFile;
	}

	public double getDeltaThr() {
		return deltaThr;
	}

	public void setDeltaThr(double deltaThr) {
		this.deltaThr = deltaThr;
	}

	public double getAlphaThr() {
		return alphaThr;
	}

	public void setAlphaThr(double alphaThr) {
		this.alphaThr = alphaThr;
	}

	public double getSpindleThr() {
		return spindleThr;
	}

	public void setSpindleThr(double spindleThr) {
		this.spindleThr = spindleThr;
	}

	public double getEmgTone() {
		return emgTone;
	}

	public void setEmgTone(double emgTone) {
		this.emgTone = emgTone;
	}
}
