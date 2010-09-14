/* ChannelSpaceType.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleSource;

/**
 * This enumerator says if the whole signal is in the
 * {@link MultichannelSampleSource described source} or only selected channels
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum ChannelSpaceType {

        /**
         * the whole signal is in the
         * {@link MultichannelSampleSource described source}
         */
	WHOLE_SIGNAL,
        /**
         * only selected channels are in the
         * {@link MultichannelSampleSource described source}
         */
	SELECTED

	;

}
