package org.signalml.plugin.newartifact.logic.algorithm;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;

public class PreprocessHelper {
	public static void Preprocess(double signal[][], NewArtifactConstants constants) {
		int tailLength = constants.getPaddingLength();
		int blockLength = constants.getBlockLength();

		double min, max;
		double rangeMin;
		double value;
		int rangeMinPos = 0;

		while (true) {
			rangeMin = Double.POSITIVE_INFINITY;
			for (int i = 0; i < signal.length; ++i) {
				min = Double.POSITIVE_INFINITY;
				max = Double.NEGATIVE_INFINITY;
				for (int j = tailLength; j < tailLength + blockLength; ++j) {
					value = signal[i][j];
					if (value < min) {
						min = value;
					}
					if (value > max) {
						max = value;
					}
				}
				if (max - min < rangeMin) {
					rangeMin = max - min;
					rangeMinPos = i;
				}
			}

			if (rangeMin != 0.0) {
				break;
			}

			signal[rangeMinPos][tailLength] = 1;
			for (int j = tailLength + 1; j < tailLength + blockLength; ++j) {
				signal[rangeMinPos][j] = 0;
			}
		}
	}
}
