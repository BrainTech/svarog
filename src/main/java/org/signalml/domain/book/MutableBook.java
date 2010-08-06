/* MutableBook.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

/** MutableBook
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MutableBook extends StandardBook {

	public static final String BOOK_COMMENT_PROPERTY = "bookComment";
	public static final String ENERGY_PERCENT_PROPERTY = "energyPercent";
	public static final String MAX_ITERATION_COUNT_PROPERTY = "maxIterationCount";
	public static final String DICTIONARY_SIZE_PROPERTY = "dictionarySizeProperty";
	public static final String DICTIONARY_TYPE_PROPERTY = "dictionaryTypeProperty";
	public static final String SAMPLING_FREQUENCY_PROPERTY = "samplingFrequencyProperty";
	public static final String CALIBRATION_PROPERTY = "calibrationProperty";
	public static final String SIGNAL_CHANNEL_COUNT_PROPERTY = "signalChannelCount";
	public static final String TEXT_INFO_PROPERTY = "textInfo";
	public static final String WEB_SITE_INFO_PROPERTY = "webSiteInfo";
	public static final String DATE_PROPERTY = "date";
	public static final String CHANNEL_LABEL_PROPERTY = "channelLabel";

	/** Sets book comment.
	 *
	 * @param comment
	 */
	void setBookComment(String comment);

	/** Sets target energy percent (decomposition).
	 *
	 * @param energyPercent
	 */
	void setEnergyPercent(float energyPercent);

	/** Set decomposition max iter. cnt.
	 *
	 * @param maxIterationCount
	 */
	void setMaxIterationCount(int maxIterationCount);

	/** Set decomposition dictionary size.
	 *
	 * @param dictionarySize
	 */
	void setDictionarySize(int dictionarySize);

	/** Set decomposition dictionary type.
	 *
	 * @param dictionaryType
	 */
	void setDictionaryType(char dictionaryType);

	/** Set sampling frequency.
	 *
	 * @param samplingFrequency
	 */
	void setSamplingFrequency(float samplingFrequency);

	/** Set calibration.
	 *
	 * @param calibration
	 */
	void setCalibration(float calibration);

	/** Set original signal channel count.
	 *
	 * @param signalChannelCount
	 */
	void setSignalChannelCount(int signalChannelCount);

	/** Set text info (TEXT_INFO field).
	 *
	 * @param textInfo
	 */
	void setTextInfo(String textInfo);

	/** Set web site info.
	 *
	 * @param webSiteInfo
	 */
	void setWebSiteInfo(String webSiteInfo);

	/** Set book date.
	 *
	 * @param date
	 */
	void setDate(String date);

	/** Set the label of the given channel (call may be disregarded if channel labels
	 *  are not supported).
	 *
	 * @param channelIndex
	 * @param label
	 */
	void setChannelLabel(int channelIndex, String label);

	/** Create compatible mutable segments (number equal to channel count),
	 *  add them at the end of the book, and return them, so that they can
	 *  be configured and filled with atoms.
	 *
	 *  Returned segments should have segmentNumber and channelNumber set
	 *  accordingly to where they are located in this book.
	 *
	 *
	 */
	MutableBookSegment[] addNewSegment(float segmentTime, int segmentLength);

	/** Add an array of segments to the end of the book. The length of the array
	 *  should correspond to the number of channels.
	 *
	 *  Segment and channel indices given in the segments are not taken into
	 *  account.
	 *
	 * @param segments
	 * @throws IllegalArgumentException when the length of array is different from channel count
	 */
	int addSegment(StandardBookSegment[] segments) throws IllegalArgumentException;

	/** Sets book segments for the given segment index.
	 *
	 *  Segment and channel indices given in the segments are not taken into
	 *  account.
	 *
	 * @param segmentIndex
	 * @param segments
	 */
	void setSegmentAt(int segmentIndex, StandardBookSegment[] segments);

	/** Sets the segment for the given segment and channel.
	 *
	 * 	Segment and channel indices given in the segment are not taken into
	 *  account.
	 *
	 * @param segmentIndex
	 * @param channelIndex
	 * @param segment
	 */
	void setSegmentAt(int segmentIndex, int channelIndex, StandardBookSegment segment);

	/** Removes given segment from the book (all channels) and returns them.
	 *
	 * @param segmentIndex
	 *
	 */
	StandardBookSegment[] removeSegmentAt(int segmentIndex);

	/** Clears the book (removes all segments).
	 *
	 */
	void clear();

}
