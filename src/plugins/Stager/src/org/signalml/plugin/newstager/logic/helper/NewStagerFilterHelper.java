package org.signalml.plugin.newstager.logic.helper;

public class NewStagerFilterHelper {

	public static double[] LowPassFilter(double signal[], double num[],
					     double den[]) {
		int blockCount = signal.length;
		int numLength = num.length;
		int denLength = den.length;

		double result[] = new double[blockCount];

		for (int i = 0; i < blockCount; ++i) {
			double value = 0d;
			for (int k = 0; k < Math.min(blockCount, numLength) && k <= i; ++k) {
				value += num[k] * signal[i - k];
			}
			for (int k = 1; k < Math.min(blockCount, denLength) && k <= i; ++k) {
				value -= den[k] * result[i - k];
			}
			result[i] = value;
		}

		return result;
	}

}
