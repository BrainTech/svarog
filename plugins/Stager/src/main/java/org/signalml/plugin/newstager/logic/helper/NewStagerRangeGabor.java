package org.signalml.plugin.newstager.logic.helper;

public class NewStagerRangeGabor {
	private static int DFBOUND = 3;

	private static interface Predicate {
		public boolean keepLooping(double v);
	}

	private static interface Step {
		public double newValue(double v);
	}

	public static double HRangeGabor(int signalSize, double signalSampling,
			double width, double frequency, double position, double amplitude,
			double phase) {
		if (width == signalSize) {
			return amplitude;
		} else if (width == 0) {
			return 0.0d;
		} else if (frequency == 0.0d) {
			return 0.5d * amplitude;
		} else {
			return DoCompute(signalSize, signalSampling, width, frequency,
					position, amplitude, phase);
		}
	}

	protected static double DoCompute(int signalSize, double signalSampling,
			double width, double frequency, double position, double amplitude,
			double phase) {
		final double p = position * signalSampling;
		final double tb = 0.0d;
		final double te = ((double) signalSize) * signalSampling - 1;
		final double w = width * signalSampling;
		final double f = Math.PI * frequency / (0.5d * signalSampling);
		final double a = 0.5d * amplitude;

		double tpp = Math.min(Math.max(Math.round(p), tb), te);
		double hb = Coeff(a, tb - p, w, f, phase);
		double he = Coeff(a, te - p, w, f, phase);
		double hp = Coeff(a, tpp - p, w, f, phase);

		double h1[] = ComputeRange(phase, p, w, f, a, tpp, hp, new Predicate() {

			@Override
			public boolean keepLooping(double v) {
				return v > 0.0d;
			}

		}, new Step() {

			@Override
			public double newValue(double v) {
				return v - 1;
			}
		});

		double h2[] = ComputeRange(phase, p, w, f, a, tpp, hp, new Predicate() {

			@Override
			public boolean keepLooping(double v) {
				return v < te;
			}

		}, new Step() {

			@Override
			public double newValue(double v) {
				return v + 1;
			}

		});

		return (Math.max(Math.max(hb, he), Math.max(h1[1], h2[1])) - 
				Math.min(Math.min(hb, he), Math.min(h1[0], h2[0])));
	}

	private static double[] ComputeRange(double phase, double p, double w,
			double f, double a, double tp, double hn, Predicate loopCondition,
			Step updater) {
		double ho;
		double hmin = hn;
		double hmax = hn;
		int signOld, sign = 0;
		int dfps = 0;
		while (loopCondition.keepLooping(tp) && dfps < DFBOUND) {
			tp = updater.newValue(tp);

			ho = hn;
			hn = Coeff(a, tp - p, w, f, phase);
			signOld = sign;
			sign = (int) Math.signum(hn - ho);
			if (sign != signOld) {
				++dfps;
			}
			hmin = Math.min(hmin, hn);
			hmax = Math.max(hmax, hn);
		}
		return new double[] { hmin, hmax };
	}

	private static double Coeff(double amplitude, double arg, double w,
			double f, double phase) {
		double v = arg / w;
		return amplitude * Math.exp(-Math.PI * v * v)
				* Math.cos(f * arg + phase);
	}
}
