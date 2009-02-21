/* MarkerTimeSpace.java created 2008-01-18
 * 
 */

package org.signalml.domain.signal.space;

/** MarkerTimeSpace
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MarkerTimeSpace {

	private int markerChannel;
	private String markerStyleName;
	
	private double secondsBefore;
	private double secondsAfter;

	public int getMarkerChannel() {
		return markerChannel;
	}

	public void setMarkerChannel(int markerChannel) {
		this.markerChannel = markerChannel;
	}

	public String getMarkerStyleName() {
		return markerStyleName;
	}

	public void setMarkerStyleName(String markerStyleName) {
		this.markerStyleName = markerStyleName;
	}

	public double getSecondsBefore() {
		return secondsBefore;
	}

	public void setSecondsBefore(double secondsBefore) {
		this.secondsBefore = secondsBefore;
	}

	public double getSecondsAfter() {
		return secondsAfter;
	}

	public void setSecondsAfter(double secondsAfter) {
		this.secondsAfter = secondsAfter;
	}
		
}
