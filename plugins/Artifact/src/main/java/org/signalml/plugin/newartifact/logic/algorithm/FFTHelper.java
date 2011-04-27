package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFTHelper {
	private final DoubleFFT_1D fft;
	private final int length;
	private final int targetLength;

	public FFTHelper(int length, int targetLength) {
		this.fft = new DoubleFFT_1D(length);
		this.length = length;
		this.targetLength = targetLength;
	}

	public double fft(double source[], double target[]) {
		double sum = 0.0;

		for (int j = 0; j < length; ++j) {
			target[j] = source[j];
		}
		Arrays.fill(target, length, (length << 1) - 1, 0.0);
		fft.realForwardFull(target);
		for (int j = 0; j < this.targetLength; j += 2) {
			target[j] = (target[j] * target[j] + target[j + 1] * target[j + 1]) / this.length;
			sum += target[j];
		}

		return sum;
	}
}
