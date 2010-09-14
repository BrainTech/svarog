/* SignalSelectionSampleSource.java created 2007-11-02
 *
 */

package org.signalml.domain.signal;

import org.signalml.app.view.signal.SampleSourceUtils;

/**
 * This class represents the {@link MultichannelSampleSource soruce} of samples
 * for the fragment of multichannel signal. This fragment is created from
 * a given {@link SignalSelection selection}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionSampleSource extends SignalFragmentSampleSource {

        /**
         * Constructor. Creates the {@link MultichannelSampleSource soruce}
         * of samples for the fragment of multichannel signal using a given
         * {@link SignalSelection selection} to specify the fragment.
         * @param source the actual source of samples
         * @param signalSelection the selection used to specify the fragment
         */
	public SignalSelectionSampleSource(MultichannelSampleSource source, SignalSelection signalSelection) {
		super(source);

		float samplingFrequency = source.getSamplingFrequency();

		int maxSampleCount = SampleSourceUtils.getMaxSampleCount(source);

		minSample = (int) Math.floor(signalSelection.getPosition()*samplingFrequency);
		minSample = Math.max(0, Math.min(maxSampleCount, minSample));

		maxSample = (int) Math.ceil((signalSelection.getPosition() + signalSelection.getLength()) * samplingFrequency);
		maxSample = Math.max(0, Math.min(maxSampleCount, maxSample));

		length = 1 + (maxSample - minSample);

		SignalSelectionType type = signalSelection.getType();
		if (type.isChannel()) {
			channel = signalSelection.getChannel();
		} else {
			channel = SignalSelection.CHANNEL_NULL;
		}

	}


}
