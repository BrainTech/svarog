/* SignalSourceLevel.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleMontage;
import org.signalml.domain.signal.filter.MultichannelSampleFilter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * This class tells if the {@link MultichannelSampleSource source} had been
 * {@link MultichannelSampleMontage assembled} and
 * {@link MultichannelSampleFilter filtered}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SignalSourceLevel {

	/**
	 * the signal is neither assembled nor filtered
	 */
	RAW,
	/**
	 * the signal is assembled but not filtered
	 */
	ASSEMBLED,
	/**
	 * the signal is assembled and filtered
	 */
	FILTERED

	;

}
