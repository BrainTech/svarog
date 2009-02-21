/* SaveConfigDescriptor.java created 2008-01-31
 * 
 */

package org.signalml.app.method.mp5;

/** SaveConfigDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5SaveForLaterUseDescriptor {

	private boolean saveConfig;
	private boolean saveSignal;
	
	public boolean isSaveConfig() {
		return saveConfig;
	}
	
	public void setSaveConfig(boolean saveConfig) {
		this.saveConfig = saveConfig;
	}
	
	public boolean isSaveSignal() {
		return saveSignal;
	}
	
	public void setSaveSignal(boolean saveSignal) {
		this.saveSignal = saveSignal;
	}
		
}
