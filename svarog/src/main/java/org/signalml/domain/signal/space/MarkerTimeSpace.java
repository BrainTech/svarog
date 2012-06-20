/* MarkerTimeSpace.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * This class describes the neighbourhood of a marker.
 * Contains the number of channel in which the marker is located and
 * time before and after the marker that should be included in this space.
 * Also includes the desciption of the style of the marker.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MarkerTimeSpace {

	/**
	 * the index of the marker channel in the
	 * {@link MultichannelSampleSource source}
	 */
	private int markerChannel;
	/**
	 * the name of the type (style) of a marker
	 */
	private String markerStyleName;

	/**
	 * the length (in seconds) before the marker that should be included in
	 * the segment
	 */
	private double secondsBefore;

	/**
	 * the length (in seconds) before the marker that should be included in
	 * the segment
	 */
	private double secondsAfter;

	/**
	 * Returns the index of the marker channel in the
	 * {@link MultichannelSampleSource source}.
	 * @return the index of the marker channel in the source
	 */
	public int getMarkerChannel() {
		return markerChannel;
	}

	/**
	 * Sets the index of the marker channel in the
	 * {@link MultichannelSampleSource source}.
	 * @param markerChannel the index of the marker channel in the source
	 */
	public void setMarkerChannel(int markerChannel) {
		this.markerChannel = markerChannel;
	}

	/**
	 * Returns the name of the type (style) of a marker.
	 * @return the name of the type (style) of a marker
	 */
	public String getMarkerStyleName() {
		return markerStyleName;
	}

	/**
	 * Sets the name of the type (style) of a marker
	 * @param markerStyleName the name of the type (style) of a marker
	 */
	public void setMarkerStyleName(String markerStyleName) {
		this.markerStyleName = markerStyleName;
	}

	/**
	 * Returns the length (in seconds) before the marker that should be
	 * included in the segment
	 * @return the length (in seconds) before the marker that should be
	 * included in the segment
	 */
	public double getSecondsBefore() {
		return secondsBefore;
	}

	/**
	 * Sets the length (in seconds) before the marker that should be
	 * included in the segment
	 * @param secondsBefore the length (in seconds) before the marker that
	 * should be included in the segment
	 */
	public void setSecondsBefore(double secondsBefore) {
		this.secondsBefore = secondsBefore;
	}

	/**
	 * Returns the length (in seconds) after the marker that should be
	 * included in the segment
	 * @return the length (in seconds) after the marker that should be
	 * included in the segment
	 */
	public double getSecondsAfter() {
		return secondsAfter;
	}

	/**
	 * Sets the length (in seconds) after the marker that should be
	 * included in the segment
	 * @param secondsAfter the length (in seconds) after the marker that
	 * should be included in the segment
	 */
	public void setSecondsAfter(double secondsAfter) {
		this.secondsAfter = secondsAfter;
	}

}
