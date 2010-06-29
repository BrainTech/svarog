/* StandardBook.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

import java.util.Enumeration;

/** StandardBook
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface StandardBook {

	/** Format version.
	 *
	 * @return
	 */
	String getVersion();

	/** Book comment (## in mp5 config)
	 *
	 * @return
	 */
	String getBookComment();

	/** Decomposition energy percent.
	 *
	 * @return
	 */
	float getEnergyPercent();

	/** Max number of iterations.
	 *
	 * @return
	 */
	int getMaxIterationCount();

	/** Dictionary size.
	 *
	 * @return
	 */
	int getDictionarySize();

	/** Dictionary types (possible value info needed)
	 *
	 * @return
	 */
	char getDictionaryType();

	/** Signal sampling frequency Hz.
	 *
	 * @return
	 */
	float getSamplingFrequency();

	/** Calibration (~ points per microvolt), interpreted as: 1 uV = 1 sample * calibration
	 *
	 * @return
	 */
	float getCalibration();

	/** Number of channels in signal file (same as channel count?).
	 *
	 * @return
	 */
	int getSignalChannelCount();

	/** Text info (TEXT_INFO header)
	 *
	 * @return
	 */
	String getTextInfo();

	/** Web site info.
	 *
	 * @return
	 */
	String getWebSiteInfo();

	/** Book date.
	 *
	 * @return
	 */
	String getDate();

	/** Number of channels in book file (each segment is assumed to contain this number of versions, one
	 *  for each channel).
	 *
	 * @return
	 */
	int getChannelCount();

	/** Get the label of the given channel.
	 *
	 * @param channelIndex
	 * @return null if information is not available
	 */
	String getChannelLabel(int channelIndex);

	/** Number of segments in file (regardles of the number of channels, so a
	 *  total number of StandardBookSegments obtainable should equal
	 *  getSegmentCount() * getChannelCount()
	 *
	 * @return
	 */
	int getSegmentCount();

	/** Should return an array of "pages", one entry per channel.
	 *
	 * @param segmentIndex
	 * @return
	 */
	StandardBookSegment[] getSegmentAt(int segmentIndex);

	/** "page" for given segment and channel
	 *
	 * @param segmentIndex
	 * @param channelIndex
	 * @return
	 */
	StandardBookSegment getSegmentAt(int segmentIndex, int channelIndex);

	/** The names of additional (version or format specific) properties of this book.
	 *
	 * @return
	 */
	Enumeration<String> getPropertyNames();

	/** Obtain named additional property
	 *
	 * @param name
	 * @return null
	 * @throws IllegalArgumentException on unsupported property name
	 */
	Object getProperty(String name) throws IllegalArgumentException;

	/** Closes this book (if the book is connected to a file resource this
	 *  file resource should be released).
	 *
	 */
	void close();

}
