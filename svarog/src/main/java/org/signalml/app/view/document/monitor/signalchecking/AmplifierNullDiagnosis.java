/* AmplifierNullDiagnosis.java created 2010-10-26
 *
 */

package org.signalml.app.view.document.monitor.signalchecking;

import java.util.HashMap;
import java.util.List;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;

/**
 * A {@link GenericAmplifierDiagnosis} class that checks whether signal value
 * is equal to amp null.
 * <ul>
 * Parameters that must be passed (keys):
 * <li>{@link #TEST_TOLERANCE}</li>
 * <li>parameters inherited from {@link GenericAmplifierDiagnosis}:
 * <ul><li>{@link GenericAmplifierDiagnosis#SAMPLES_TESTED_FACTOR}</li></ul>
 * </ul>
 * @author Tomasz Sawicki
 */
public class AmplifierNullDiagnosis extends GenericAmplifierDiagnosis {

	/**
	 * {@link #testTolerance} parameter key.
	 */
	public static final String TEST_TOLERANCE = "TestTolerance";

	/**
	 * Tolerance of the test. The smaller this constant is the more samples will
	 * be stated as invalid. Needs to be passed as a parameter.
	 */
	private double testTolerance;

	/**
	 * An array of values (one for each channel) used to check if a sample from
	 * that channel is valid. If |sample - offset| is less than maxAmpC then the sample is valid.
	 */
	private double[] maxAmpC;

	/**
	 * Constructor.
	 * @param monitorSignalDocument {@link GenericAmplifierDiagnosis#monitorSignalDocument}
	 * @param parameters {@link GenericAmplifierDiagnosis#parameters}
	 */
	public AmplifierNullDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {

		super(monitorSignalDocument, parameters);
		initialize();
	}

	/**
	 * Initializes the diagnosing unit.
	 */
	private void initialize() {

		testTolerance = Double.parseDouble(getParameters().get(TEST_TOLERANCE).toString());

		maxAmpC = new double[getChannelCount()];
		List<AmplifierChannel> channels = getMonitorSignalDocument().getExperimentDescriptor().getAmplifier().getSelectedChannels();

		for (int i = 0; i < getChannelCount(); i++)
			maxAmpC[i] = testTolerance * Math.abs(channels.get(i).getIdle());

	}

	/**
	 * Returns an information on each channel based on the information
	 * from the {@link MonitorSignalDocument} object. Calls {@link #channelValid(int)}
	 * for each channel in the signal.
	 *
	 * @return a HashMap<String, ChannelState> - the key is channel's label,
	 * the value - channel state.
	 */
	@Override
	public HashMap<String, ChannelState> signalState() {

		if (!areEnoughSamples())
			return null;

		HashMap<String, ChannelState> channels = new HashMap<>();

		for (int i = 0; i < getChannelCount(); i++)
			channels.put(getLabel(i), new ChannelState(channelValid(i), null));

		return channels;
	}

	/**
	 * Checks if signal from a given channel is valid. It gets
	 * {@link GenericAmplifierDiagnosis#samplesToTest} samples, then calls
	 * {@link #sampleValid(double, int)} method for each one.
	 *
	 * @param channel Number of the channel
	 * @return false if all samples were invalid, true if at least one was valid
	 */
	private boolean channelValid(int channel) {

		double[] samples = getSamplesForAChannel(channel);

		for (int i = 0; i < getSamplesToTest(); i++)
			if (sampleValid(samples[i], channel)) return true;

		return false;
	}

	/**
	 * Checks if a sample is valid.
	 *
	 * @param sample Value of the sample
	 * @param channel Number of the channel from which the sample was collected
	 * @return true if the sample is valid, false if it's not
	 */
	private boolean sampleValid(double sample, int channel) {
		return Math.abs(sample) < maxAmpC[channel];
	}
}