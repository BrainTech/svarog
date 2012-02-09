package org.signalml.math.fft;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.signalml.math.iirdesigner.ArrayOperations;

/**
 * This class can be used to calculate FFT of a signal.
 *
 * @author Piotr Szachewicz
 */
public class FourierTransform {

	/**
	 * The {@link WindowFunction} to be used to window the input signal.
	 */
	private WindowFunction windowFunction = new WindowFunction(WindowType.RECTANGULAR);

	/**
	 * Constructor.
	 * @param windowType the window to be used before performing FFT
	 * @param parameter the parametr for the specified window
	 */
	public FourierTransform(WindowType windowType, double parameter) {
		this.windowFunction = new WindowFunction(windowType, parameter);
	}

	/**
	 * Constructor.
	 * @param windowType the window to be applied to the signal before actual
	 * FFT is performed
	 */
	public FourierTransform(WindowType windowType) {
		this.windowFunction = new WindowFunction(windowType);
	}

	public FourierTransform() {
	}

	/**
	 * Calculates the FFT of the given signal.
	 *
	 * If a {@link WindowType} was specified while calling a constructor
	 * it is applied to the signal before processing.
	 *
	 * If the signal's length is not a power of 2 (which is required by the
	 * FFT algorithm) this function automatically pads it with zeros.
	 *
	 * @param signal the signal for which the FFT should be calculated
	 * @return the result of the FFT
	 */
	public Complex[] forwardFFT(double[] signal) {
		FastFourierTransformer transformer = new FastFourierTransformer();
		double[] windowedSignal = windowFunction.applyWindow(signal);
		double[] paddedSignal = padWithZeroIfNecessary(windowedSignal);
		return transformer.transform(paddedSignal);

	}

	/**
	 * Calculates the inverse FFT of the frequency domain representation
	 * of the signal. That is: converts the given data from frequency domain
	 * to the time domain.
	 * @param fft the frequency domain representation of the signal
	 * (e.g. the output of the {@link FourierTransform#forwardFFT(double[])}
	 * @return the time domain representation of the signal
	 */
	public double[] inverseFFT(Complex[] fft) {
		FastFourierTransformer transformer = new FastFourierTransformer();
		Complex[] complexSignal = transformer.inversetransform(fft);

		double[] signal = new double[complexSignal.length];
		for (int i = 0; i < complexSignal.length; i++) {
			signal[i] = complexSignal[i].getReal();
		}
		return signal;
	}

	/**
	 * Returns a number that is greater or equal to the given and is
	 * power of 2.
	 * @param initialSize the value which is smaller or equal to the returned
	 * value
	 * @return a number that is a power of two and is not less that initialSize
	 */
	public static int getPowerOfTwoSize(int initialSize) {
		double log_of_initialSize_to_base_2 = Math.log(initialSize) / Math.log(2);
		return (int) Math.pow(2, Math.ceil(log_of_initialSize_to_base_2));
	}

	/**
	 * Pads the given signal so that its length is a power of two
	 * (or performs no padding if its length is already a power of two).
	 * @param signal the signal to be padded
	 * @return a padded with zeros version of the signal
	 */
	public static double[] padWithZeroIfNecessary(double[] signal) {
		int powerOfTwoSize = getPowerOfTwoSize(signal.length);
		if (powerOfTwoSize != signal.length) {
			return ArrayOperations.padArrayWithZerosToSize(signal, powerOfTwoSize);
		} else {
			return signal;
		}
	}

	/**
	 * Returns frequencies for elements returned by the {@link FourierTransform#forwardFFT(double[])}.
	 * @param signal
	 * @param samplingFrequency
	 * @return
	 */
	public static double[] getFrequencies(double[] signal, double samplingFrequency) {
		int size = getPowerOfTwoSize(signal.length);

		// the other half of frequencies are the same (mirrored)
		double[] frequencies = new double[size / 2];

		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] = i * samplingFrequency / size;
		}

		return frequencies;
		
		/*/double step = samplingFrequency / N;
        double[] result = new double[N];
        for (int i = 0; i < (N + 1) / 2; i++) {
                result[i] = step * i;
        }
        for (int i = (N + 1) / 2; i < N; i++) {
                result[i] = -(step * (N - i));
        }
        return result;*/
	}

}
