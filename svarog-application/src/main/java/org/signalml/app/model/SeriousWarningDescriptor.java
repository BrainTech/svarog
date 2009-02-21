/* SeriousWarningDescriptor.java created 2007-11-22
 * 
 */

package org.signalml.app.model;

/** SeriousWarningDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SeriousWarningDescriptor {

	private String warning;
	private int timeout;
	
	public SeriousWarningDescriptor() {
	}
	
	public SeriousWarningDescriptor(String warning, int timeout) {
		this.warning = warning;
		this.timeout = timeout;
	}
	
	public String getWarning() {
		return warning;
	}
	
	public void setWarning(String warning) {
		this.warning = warning;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}	
	
}
