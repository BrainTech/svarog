package org.signalml.fft;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import org.apache.commons.math.complex.Complex;

/**
 * Class, which allows to:
 * <ul>
 * <li>compute forward and inverse FFT of real and complex data,</li>
 * <li>calculate the power spectrum of the data (using FFT),</li>
 * <li>apply windows to the data before computing FFT/power spectrum.</li>
 * </ul>
 * <p>
 * To actual calculation of the FFT is performed using Piotr Wendykier's JTransforms
 * (class {@code DoubleFFT_1D}).
 *
 * @author Marcin Szumski
 */
public class FourierTransform {

	/**
	 * the type of the window function
	 */
	private WindowType windowType = WindowType.RECTANGULAR;
	/**
	 * the parameter of the window function
	 */
	private double windowParameter = 0;
	/**
	 * the calculated weights of the window function;
	 * the signal is multiplied by these weights to obtain windowed data
	 */
	private double[] windowWeights = null;
	/**
	 * the calculates sum of squares of {@link #windowWeights weights}
	 */
	private double windowWeightsSqueredSum = 0;

	/**
	 * Constructor.
	 */
	public FourierTransform() {
	}

	/**
	 * Sets the {@link WindowType type} of the window, the parameter and if any
	 * of these values is different then formerly invalidates calculated weights.
	 * @param type the type of the window
	 * @param parameter the parameter of the window - it is used only for
	 * {@link WindowType#GAUSSIAN GAUSSIAN} and {@link WindowType#KAISER
	 * KAISER} window
	 */
	public void setWindowType(WindowType type, double parameter) {
		if (type != windowType || windowParameter != parameter) {
			windowWeights = null;
		}
		this.windowType = type;
		windowParameter = parameter;
	}

	/**
	 * Calculates the square of the double value.
	 * @param i the value to be squared
	 * @return the square of the double value
	 */
	private double square(double i) {
		return i * i;
	}

	/**
	 * Calculates the weights for the window of a specified length using the
	 * {@link #windowType type} of the window and the {@link #windowParameter
	 * parameter}. Calculated weights are put in {@link #windowWeights}.
	 * <p>
	 * If the weights already exist, no action is taken.
	 * <p>
	 * Apart from that calculates the sum of squared weights and stores the
	 * result in {@link #windowWeightsSqueredSum}.
	 * @param length the length of the window (number of samples)
	 */
	private void calculateWeights(int length) {
		if (windowWeights != null && windowWeights.length == length) {
			return;
		}
		windowWeights = new double[length];
		double n = length;
		switch (windowType) {
			case BARTLETT:
				for (int i = 0; i < length; ++i) {
					windowWeights[i] = 1.0D - Math.abs((2.0D * i - n + 1.0D) / (n - 1.0D));
				}
				break;
			case GAUSSIAN:
				for (int i = 0; i < length; ++i) {
					windowWeights[i] = Math.exp((-0.5D) * square(windowParameter * ((2.0D * i - n + 1.0D) / (n - 1.0D))));
				}
				break;
			case HAMMING:
				for (int i = 0; i < length; ++i) {
					windowWeights[i] = 0.54D - 0.46D * Math.cos(2.0D * i * Math.PI / (n - 1.0D));
				}
				break;
			case HANN:
				for (int i = 0; i < length; ++i) {
					windowWeights[i] = 0.5D * (1.0D - Math.cos(2.0D * i * Math.PI / (n - 1.0D)));
				}
				break;
			case KAISER:

			//TODO kaiser window
			case RECTANGULAR:
				for (int i = 0; i < length; ++i) {
					windowWeights[i] = 1.0D;
				}
				break;
			case WELCH:
				for (int i = 0; i < length; ++i) {
					windowWeights[i] = 1.0D - square((2.0D * i - n + 1.0D) / (n - 1.0D));
				}
				break;
		}
		windowWeightsSqueredSum = 0;
		for (int i = 0; i < windowWeights.length; ++i) {
			windowWeightsSqueredSum += square(windowWeights[i]);
		}
	}

	/**
	 * Applies the window to the real data and returns the array with
	 * windowed data.
	 * <p>
	 * The array with windowed data is twice as large as the original data
	 * because the (real) values are stored only in cells with even indexes
	 * (odd indexes are reserved for imaginary values).
	 * @param data the data to be windowed
	 * @return the array with windowed data
	 */
	private double[] applyWindowReal(double[] data) {
		calculateWeights(data.length);
		double[] windowedData = new double[2 * data.length];
		for (int i = 0; i < data.length; ++i) {
			windowedData[2 * i + 1] = 0;
			windowedData[2 * i] = windowWeights[i] * data[i];
		}
		return windowedData;
	}

	/**
	 * Applies the window to the complex data and returns the array with
	 * windowed data.
	 * <p>
	 * Both arrays (for original and windowed data) have format:<br>
	 * {@code arr[2*k]} - the real part of k-th sample<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th sample
	 * @param data the data to be windowed
	 * @return the array with windowed data
	 */
	private double[] applyWindowComplex(double[] data) {
		calculateWeights(data.length / 2);
		double[] windowedData = new double[data.length];
		for (int i = 0; i < data.length / 2; ++i) {
			windowedData[2 * i] = windowWeights[i] * data[2 * i];
			windowedData[2 * i + 1] = windowWeights[i] * data[2 * i + 1];
		}
		return windowedData;
	}

	/**
	 * Applies the function inverse to the window function - instead of
	 * multiplying by coefficients divides by them.
	 * <p>
	 * Both arrays (for original and unwindowed data) have format:<br>
	 * {@code arr[2*k]} - the real part of k-th sample<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th sample
	 * @param data the data to be "unwindowed"
	 * @return the result
	 */
	private double[] applyWindowInverse(double[] data) {
		calculateWeights(data.length / 2);
		double[] unwindowedData = new double[data.length];
		for (int i = 0; i < data.length / 2; ++i) {
			unwindowedData[2 * i] = data[2 * i] / windowWeights[i];
			unwindowedData[2 * i + 1] = data[2 * i + 1] / windowWeights[i];
		}
		return unwindowedData;
	}

	/**
	 * Calculates the FFT of real data and returns the result.
	 * Before the real calculation the window is applied to the data.
	 * <p>
	 * The result array contains complex numbers in format:
	 * <br>
	 * {@code result[2*k]} - the real part of k-th element<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th element
	 * @param data the real data to be transformed
	 * @return the result of FFT
	 */
	public double[] forwardFFTReal(double[] data) {
		double[] transformed = applyWindowReal(data);
		DoubleFFT_1D fft = new DoubleFFT_1D(data.length);
		fft.complexForward(transformed);
		return transformed;
	}

	/**
	 * Calculates the FFT of the complex data and returns the result.
	 * Before the real calculation the window is applied to the data.
	 * <p>
	 * Both arrays contain complex data in format:<br>
	 * {@code result[2*k]} - the real part of k-th element<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th element
	 * @param data the data to be transformed
	 * @return the result of FFT
	 */
	public double[] forwardFFTComplex(double[] data) {
		double[] transformed = applyWindowComplex(data);
		DoubleFFT_1D fft = new DoubleFFT_1D(data.length / 2);
		fft.complexForward(transformed);
		return transformed;
	}

	/**
	 * Calculates the FFT of complex data. Also: if a window is set,
	 * it is applied to the given data.
	 * @param data the data to be transformed
	 * @return the result of FFT
	 */
	public Complex[] forwardFFTComplex(Complex[] data) {
		double[] interleaved = convertComplexToInterleaved(data);
		double[] transformedInterleaved = forwardFFTComplex(interleaved);
		Complex[] transformedComplex = convertInterleavedToComplex(transformedInterleaved);
		return transformedComplex;
	}

	/**
	 * Converts complex number array to a double array in which
	 * real and imaginary parts of the complex numbers are interleaved
	 * that is:
	 * result[2*k] - the real part of k-th element and
	 * result[2*k+1] - the imaginary part of k-th element.
	 * @param complex the complex array to be converted
	 * @return interleaved complex data
	 */
	private double[] convertComplexToInterleaved(Complex[] complex) {
		double[] interleaved = new double[2 * complex.length];

		for (int i = 0; i < complex.length; i++) {
			interleaved[2 * i] = complex[i].getReal();
			interleaved[2 * i + 1] = complex[i].getImaginary();
		}

		return interleaved;
	}

	/**
	 * Converts an array in which real and imaginary parts of the complex
	 * numbers are interleaved to an equivalent array of complex numbers.
	 * @param interleaved interleaved data to be converted
	 * @return converted array
	 */
	private Complex[] convertInterleavedToComplex(double[] interleaved) {
		Complex[] complex = new Complex[interleaved.length / 2];

		for (int i = 0; i < complex.length; i++) {
			complex[i] = new Complex(interleaved[2 * i], interleaved[2 * i + 1]);
		}

		return complex;
	}

	/**
	 * Calculates the inverse FFT of the real data and applies the "inverse"
	 * window to the result.
	 * The returned array contains complex data in format:<br>
	 * {@code result[2*k]} - the real part of k-th element<br>
	 * {@code result[2*k+1]} - the imaginary part of k-th element
	 * @param data the data to transformed
	 * @return the result
	 */
	public double[] inverseFFTReal(double[] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data.length);
		double[] transformed = new double[data.length * 2];
		for (int i = 0; i < data.length; ++i) {
			transformed[2 * i] = data[i];
			transformed[2 * i + 1] = 0;
		}
		fft.complexInverse(transformed, false);
		return applyWindowInverse(transformed);
	}

	/**
	 * Calculates the inverse FFT of the complex data and applies the "inverse"
	 * window to the result.
	 * Both arrays (data and result) contain complex data in format:<br>
	 * {@code arr[2*k]} - the real part of k-th element<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th element
	 * @param data the data to transformed
	 * @return the result
	 */
	public double[] inverseFFTComplex(double[] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data.length/2);
		double[] transformed = new double[data.length];
		for (int i = 0; i < data.length; ++i) {
			transformed[i] = data[i];
		}
		fft.complexInverse(transformed, false);
		return applyWindowInverse(transformed);
	}

	/**
	 * Calculates the power spectrum based on the result of FFT.
	 * The result of FFT (contains complex data) is in form:<br>
	 * {@code arr[2*k]} - the real part of k-th element<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th element
	 * @param fftTransformedData the result of FFT
	 * @param timeDelta the number of seconds per sample
	 * @param dataLength the number of samples
	 * @return the array with two rows:
	 * <ul>
	 * <li>first row ({@code powerSpectrum[0][i]} - frequencies,</li>
	 * <li>second row ({@code powerSpectrum[1][i]}) - estimates of the
	 * power spectrum for these frequencies</li>
	 */
	private double[][] calculateSpectrum(double[] fftTransformedData, double timeDelta, int dataLength) {
		int size = dataLength / 2;
		double[][] powerSpectrum = new double[2][size];
		powerSpectrum[1][0] = square(fftTransformedData[0]) + square(fftTransformedData[1]);
		for (int i = 1; i < size; ++i) {
			powerSpectrum[1][i] = square(fftTransformedData[2 * i]) + square(fftTransformedData[2 * i + 1]) + square(fftTransformedData[2 * dataLength - 2 * i]) + square(fftTransformedData[2 * dataLength - 2 * i + 1]);
		}
		for (int i = 1; i < size; ++i) {
			powerSpectrum[1][i] = 2.0D * powerSpectrum[1][i] / (windowWeightsSqueredSum * dataLength);
			powerSpectrum[0][i] = ((double) i) / ((double) dataLength * timeDelta);
		}
		return powerSpectrum;
	}

	/**
	 * Calculates the power spectrum of the provided real data using
	 * FFT.
	 * @param data the data (real numbers)
	 * @param timeDelta the number of seconds per sample
	 * @return the array with two rows:
	 * <ul>
	 * <li>first row ({@code powerSpectrum[0][i]} - frequencies,</li>
	 * <li>second row ({@code powerSpectrum[1][i]}) - estimates of the
	 * power spectrum for these frequencies</li>
	 */
	public double[][] powerSpectrumReal(double[] data, double timeDelta) {
		double[] fftTransformedData = forwardFFTReal(data);
		int dataLength = data.length;
		return calculateSpectrum(fftTransformedData, timeDelta, dataLength);
	}

	/**
	 * Calculates the power spectrum of the provided real data using
	 * FFT.
	 * @param data the complex data in form:<br>
	 * {@code arr[2*k]} - the real part of k-th element<br>
	 * {@code arr[2*k+1]} - the imaginary part of k-th element
	 * @param timeDelta the number of seconds per sample
	 * @return the array with two rows:
	 * <ul>
	 * <li>first row ({@code powerSpectrum[0][i]} - frequencies,</li>
	 * <li>second row ({@code powerSpectrum[1][i]}) - estimates of the
	 * power spectrum for these frequencies</li>
	 */
	public double[][] powerSpectrumComplex(double[] data, double timeDelta) {
		double[] fftTransformedData = forwardFFTComplex(data);
		int dataLength = data.length / 2;
		return calculateSpectrum(fftTransformedData, timeDelta, dataLength);
	}
}
