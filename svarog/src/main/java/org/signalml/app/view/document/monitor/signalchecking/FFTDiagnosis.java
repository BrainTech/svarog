package org.signalml.app.view.document.monitor.signalchecking;

import java.util.HashMap;
import org.apache.commons.math.complex.Complex;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.montage.ElectrodeType;
import org.signalml.math.fft.FourierTransform;

/**
 * A {@link GenericAmplifierDiagnosis} class that checks whether FFT of some
 * frequencies is greater than FFT of other frequencies.
 *
 * @author Tomasz Sawicki
 */
public class FFTDiagnosis extends GenericAmplifierDiagnosis {

	/**
	 * {@link #samplesTestedFactor} parameter key.
	 */
	public static final String ELECTRODE_TYPE = "ElectrodeType";

	/**
	 * Constructor.
	 * @param monitorSignalDocument {@link GenericAmplifierDiagnosis#monitorSignalDocument}
	 * @param parameters {@link GenericAmplifierDiagnosis#parameters}
	 */
	public FFTDiagnosis(MonitorSignalDocument monitorSignalDocument, HashMap<String, Object> parameters) {
		super(monitorSignalDocument, parameters);
	}

	/**
	 * Returns an information on each channel based on the information
	 * from the {@link MonitorSignalDocument} object.
	 *
	 * @return a HashMap<String, ChannelState> - the key is channel's label,
	 * the value - channel state.
	 */
	@Override
	public HashMap<String, ChannelState> signalState() {

		HashMap<String, ChannelState> channels = new HashMap<String, ChannelState>();

		for (int i = 0; i < getRoundBuffer().getChannelCount(); i++) {
			channels.put(getLabel(i), checkChannel(i));
		}
		return channels;
	}

	/**
	 * Calculates FFT and checks whether given channel is valid.
	 * @param channelNo channel number
	 * @return channel state
	 */
	private ChannelState checkChannel(int channelNo) {

		double[] samples = getSamplesForAChannel(channelNo);
		FourierTransform fourierTransform = new FourierTransform();
		Complex[] fft = fourierTransform.forwardFFT(samples);

		double frequency = 50.0;
		//(f = n * Fs / N) => (n = f * N / Fs)
		int fftIndex = (int)(frequency * fft.length / getMonitorSignalDocument().getSamplingFrequency());

		double amplitude = fft[fftIndex].abs();
		double impedance = 2 * amplitude * 10e-6 / 10e-7;

		ElectrodeType electrodeType = (ElectrodeType) getParameters().get(ELECTRODE_TYPE);
		int max = electrodeType.getMax();
		int min = electrodeType.getMin();
		int limit = electrodeType.getLimit();
		AdditionalChannelData additionalChannelData = new AdditionalChannelData(max, min, limit, impedance, SignalCheckingMethod.FFT);
		ChannelState state = new ChannelState(true, additionalChannelData);

		return state;
	}
}
