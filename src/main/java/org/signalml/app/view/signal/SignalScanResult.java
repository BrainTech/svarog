/* SignalScanResult.java created 2008-01-30
 * 
 */

package org.signalml.app.view.signal;

/** SignalScanResult
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalScanResult {

	private double minSignalValue;
	private double maxSignalValue;
	
	public double getMinSignalValue() {
		return minSignalValue;
	}
	
	public void setMinSignalValue(double minSignalValue) {
		this.minSignalValue = minSignalValue;
	}
	
	public double getMaxSignalValue() {
		return maxSignalValue;
	}
	
	public void setMaxSignalValue(double maxSignalValue) {
		this.maxSignalValue = maxSignalValue;
	}	
	
}
