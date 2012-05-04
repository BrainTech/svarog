package org.signalml.math.fft;


/**
 * This class is capable of applying window functions ({@link WindowType})
 * to the given signal.
 *
 * @author Marcin Szumski
 * @author Piotr Szachewicz
 */
public class WindowFunction {

	/**
	 * the type of the window function
	 */
	protected WindowType windowType = WindowType.RECTANGULAR;
	/**
	 * the parameter of the window function
	 */
	protected double windowParameter = 0;
	/**
	 * the calculated weights of the window function;
	 * the signal is multiplied by these weights to obtain windowed data
	 */
	protected double[] windowWeights = null;

	/**
	 * Sets the {@link WindowType type} of the window, the parameter and if any
	 * of these values is different then formerly invalidates calculated weights.
	 * @param type the type of the window
	 * @param parameter the parameter of the window - it is used only for
	 * {@link WindowType#GAUSSIAN GAUSSIAN} and {@link WindowType#KAISER
	 * KAISER} window
	 */
	public WindowFunction(WindowType type, double parameter) {
		this.windowType = type;
		this.windowParameter = parameter;
	}

	public WindowFunction(WindowType type) {
		this.windowType = type;
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
	protected void calculateWeights(int length) {
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
	public double[] applyWindow(double[] data) {
		calculateWeights(data.length);
		double[] windowedData = new double[data.length];
		for (int i = 0; i < data.length; ++i) {
			windowedData[i] = windowWeights[i] * data[i];
		}
		return windowedData;
	}

}
