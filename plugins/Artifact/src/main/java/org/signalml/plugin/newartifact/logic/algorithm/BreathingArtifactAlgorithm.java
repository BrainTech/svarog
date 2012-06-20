package org.signalml.plugin.newartifact.logic.algorithm;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;

public class BreathingArtifactAlgorithm extends NewArtifactAlgorithmBase {

	private final FFTHelper fftHelper;

	public BreathingArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);
		this.resultBuffer = new double[1][this.constants.channelCount];
		this.fftHelper = new FFTHelper(
			this.constants.getBlockLengthWithPadding(),
			2 * this.constants.getBlockPowerVectorLength());
	}

	@Override
	public double[][] computeHead(NewArtifactAlgorithmData data) {
		return this.zeros(1, data.constants.channelCount);
	}

	@Override
	public double[][] computeTail(NewArtifactAlgorithmData data) {
		return this.zeros(1, data.constants.channelCount);
	}

	@Override
	public double[][] compute(NewArtifactAlgorithmData data) {
		double signal[][] = data.signal;
		double buffer[] = this.resultBuffer[0];

		DetrendHelper.detrend(signal);

		int length = this.constants.getBlockLengthWithPadding();
		int powerLowStart = 2 * (int) Math.floor(this.constants.getFw1()
							* this.constants.getFreqChangeCoefficient());
		int powerLowEnd = 2 * (int) Math.floor(this.constants.getFw2()
											   * this.constants.getFreqChangeCoefficient());
		int powerAllStart = 2 * (int) Math.floor(this.constants.getFs1()
							* this.constants.getFreqChangeCoefficient());
		int powerAllEnd = 2 * (int) Math.floor(this.constants.getFs2()
											   * this.constants.getFreqChangeCoefficient());
		int j;
		double[] y = new double[2 * length];
		for (int i = 0; i < signal.length; ++i) {
			double u[] = signal[i];
			double sumA = 0, sumL = 0;
			double powerSum = this.fftHelper.fft(u, y);

			for (j = powerLowStart; j <= powerLowEnd; j += 2) {
				sumL += y[j];
			}

			for (j = powerAllStart; j <= powerAllEnd; j += 2) {
				sumA += y[j];
			}

			try {
				buffer[i] = sumL
							/ (powerSum - sumA + BreathingArtifactAlgorithm.DELTA);
			} catch (ArithmeticException e) {
				buffer[i] = 0.0;
			}
		}

		return this.resultBuffer;
	}

}
