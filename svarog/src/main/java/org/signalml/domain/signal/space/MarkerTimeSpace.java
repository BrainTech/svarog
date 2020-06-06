/* MarkerTimeSpace.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import java.util.ArrayList;
import java.util.List;
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
	private List<String> markerStyleNames;

	/**
	 * The position relative to the marker, which should be included in
	 * the segment.
	 */
	private double startTime;

	/**
	 * The length (in seconds) of the segment.
	 */
	private double segmentLength;

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
		return markerStyleNames.get(0);
	}

	public List<String> getMarkerStyleNames() {
		return markerStyleNames;
	}

	public void setMarkerStyleNames(List<String> markerStyleNames) {
		this.markerStyleNames = markerStyleNames;
	}

	/**
	 * Sets the name of the type (style) of a marker
	 * @param markerStyleName the name of the type (style) of a marker
	 */
	public void setMarkerStyleName(String markerStyleName) {
		this.markerStyleNames = new ArrayList<>();
		this.markerStyleNames.add(markerStyleName);
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(double secondsAfter) {
		this.segmentLength = secondsAfter;
	}

}
