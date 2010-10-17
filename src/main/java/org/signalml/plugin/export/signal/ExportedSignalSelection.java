/**
 * 
 */
package org.signalml.plugin.export.signal;


/**
 * This is an interface for a selected part of a signal.
 * Allows to return a {@link ExportedSignalSelectionType type} of this selection,
 * position where it starts, length, number of selected channel,
 * how many segments fit in the selection and to compare selections.
 * @author Marcin Szumski
 */
public interface ExportedSignalSelection {

	/**
	 * constant saying that no channel is selected
	 */
	int CHANNEL_NULL = -1;

	/**
	 *
	 * @return type of selection
	 */
	ExportedSignalSelectionType getType();

	/**
	 *
	 * @return position where selection starts
	 */
	float getPosition();

	/**
	 *
	 * @return length of selection in seconds
	 */
	float getLength();

	/**
	 * returns position of the middle of selection
	 * @return middle of selection
	 */
	float getCenterPosition();

	/**
	 * returns position where selection is ending
	 * @return position where selection is ending
	 */
	float getEndPosition();

	/**
	 *
	 * @return number of selected channel
	 * CHANNEL_NULL when no channel is selected
	 */
	int getChannel();

	/**
	 * Assuming that segment has a given size finds that one where selection starts
	 * @param segmentSize size of a segment
	 * @return number of the segment where selection starts
	 */
	int getStartSegment(float segmentSize);

	/**
	 * Assuming that segment has a given size finds that one where selection ends
	 * @param segmentSize size of a segment
	 * @return number of the segment where selection ends
	 */
	int getEndSegment(float segmentSize);

	/**
	 * Computes how many segments of given size would fit in selection
	 * @param segmentSize size of a segment
	 * @return length of selection in segments
	 */
	int getSegmentLength(float segmentSize);

	/**
	 * Compares the current SignalSelection object with another SignalSelection object.
	 * @param s the SignalSelection that the current SignalSelection is to be compared with.
	 * @return true if the two SignalSelection objects are equal, otherwise false.
	 */
	boolean equals(ExportedSignalSelection s);

}