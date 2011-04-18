package org.signalml.plugin.newartifact.logic.stat;

public class Stat {


	public Stat() {
	}

	public double computeCorrelation(double x[], double y[]) {
		double meanx = this.mean(x);
		double meany = this.mean(y);

		double sumxDiff = 0.0d;
		double sumyDiff = 0.0d;
		double sumxyDiff = 0.0d;

		for (int i = 0; i < x.length; ++i) {
			double xDiff = x[i] - meanx;
			double yDiff = y[i] - meany;
			sumxDiff += xDiff * xDiff;
			sumyDiff += yDiff * yDiff;
			sumxyDiff += (x[i] - meanx) * (y[i] - meany);
		}

		return sumxyDiff / Math.sqrt(sumxDiff * sumyDiff);
	}


	public double mean(double x[]) {
		double mean = 0.0d;
		for (int i = 0; i < x.length; ++i) {
			mean += x[i];
		}
		return mean / x.length;
	}

}
