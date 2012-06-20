/* TimeSpaceType.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.export.signal.SignalSelection;

/**
 * This class informs if the whole length of a signal is in the
 * {@link MultichannelSampleSource described source} or it is based on
 * {@link SignalSelection selection} or neighbourhood of a marker
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TimeSpaceType {

	/**
	 * the whole length of a signal is in the
	 * {@link MultichannelSampleSource described source}
	 */
	WHOLE_SIGNAL,
	/**
	 * the {@link SignalSelection selection} part of the signal is in the
	 * {@link MultichannelSampleSource described source}
	 */
	SELECTION_BASED,
	/**
	 * the neighbourhood of the marker is in the
	 * {@link MultichannelSampleSource described source}
	 */
	MARKER_BASED

	;

}
