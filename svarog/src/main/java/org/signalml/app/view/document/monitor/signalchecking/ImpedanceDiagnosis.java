package org.signalml.app.view.document.monitor.signalchecking;

import java.util.HashMap;
import org.signalml.app.document.MonitorSignalDocument;

/**
 * A {@link GenericAmplifierDiagnosis} class that checks whether impedance
 * value reporter by the amplifier does not exceed some predefined threshold.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ImpedanceDiagnosis extends GenericAmplifierDiagnosis {

	public static double IMPEDANCE_MAX = 10.0;  // kΩ
	public static double IMPEDANCE_LIMIT = 5.0; // kΩ

	/**
	 * Constructor.
	 * @param monitorSignalDocument {@link GenericAmplifierDiagnosis#monitorSignalDocument}
	 * @param parameters {@link GenericAmplifierDiagnosis#parameters}
	 */
	public ImpedanceDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {
		super(monitorSignalDocument, parameters);
	}

	/**
	 * Returns an information on each channel based on the impedance information
	 * from the amplifier.
	 *
	 * @return a HashMap<String, ChannelState> - the key is channel's label,
	 * the value - channel state.
	 */
	@Override
	public HashMap<String, ChannelState> signalState() {
		if (!areEnoughSamples()) {
			return null;
		}

		final int channelCount = getRoundBuffer().getChannelCount();
		HashMap<String, ChannelState> channels = new HashMap<>();

		for (int i=0; i<channelCount; i++) {
			double impedance = getImpedanceForAChannel(i);
			if (!Double.isNaN(impedance)) {
				boolean valid = (impedance <= IMPEDANCE_LIMIT);
				AdditionalChannelData additionalChannelData = new AdditionalChannelData(
					IMPEDANCE_MAX,
					0.0,
					IMPEDANCE_LIMIT,
					impedance,
					SignalCheckingMethod.IMPEDANCE
				);
				ChannelState channelState = new ChannelState(valid, additionalChannelData);
				channels.put(getLabel(i), channelState);
			}
		}

		return channels;
	}

	private double getImpedanceForAChannel(int channelNo) {
		return getRoundBuffer().getImpedance(channelNo);
	}
}
