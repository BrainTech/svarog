package org.signalml.plugin.newartifact.logic.algorithm;

import org.apache.commons.math.stat.regression.SimpleRegression;

public class DetrendHelper {
	public static void detrend(double[][] v) {
		if (v.length == 0)
			return;

		SimpleRegression regression = new SimpleRegression();

		for (int i = 0; i < v.length; ++i) {
			double y[] = v[i];

			regression.clear();
			for (int j = 0; j < y.length; ++j) {
				regression.addData(j, y[j]);
			}

			double slope = regression.getSlope();
			double intercept = regression.getIntercept();

			for (int j = 0; j < y.length; ++j) {
				y[j] -= intercept + slope * j;
			}
		}
	}
}
