/**
 *
 */
package org.signalml.plugin.export.signal;


/**
 * This is an interface for a selected part of a signal.
 * Allows to:
 * <ul>
 * <li>return the {@link ExportedSignalSelectionType type} of this selection,</li>
 * <li>return the position where it starts,</li>
 * <li>return the length,</li>
 * <li>return the number of the selected channel,</li>
 * <li>return how many segments of a given size would fit in the selection,</li>
 * <li>compare selections.</li>
 * </ul>
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
	 * @return position where selection starts on the current signal
	 */
	double getPosition();

	/**
	 *
	 * @return the timestamp of the tag. For monitor tags it is the timestamp
	 * of the tag, for offline signal selections - equal to getPosition().
	 * For future purposes also offline signal selections may have their
	 * timestamp saved in the tag file.
	 */
	double getTimestamp();

	/**
	 *
	 * @return length of selection in seconds
	 */
	double getLength();

	/**
	 * returns position of the middle of selection
	 * @return middle of selection
	 */
	double getCenterPosition();

	/**
	 * returns position where selection is ending
	 * @return position where selection is ending
	 */
	double getEndPosition();

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


	/**
	* Checks if intersection of current SignalSelection with another SignalSelection is nonempty
	* @param selection the SignalSelection that the current SignalSelection is to be intersect with
	* @return true if the two SignalSelection objects overlap, otherwise false.
	*/
	boolean overlaps(ExportedSignalSelection s);
}