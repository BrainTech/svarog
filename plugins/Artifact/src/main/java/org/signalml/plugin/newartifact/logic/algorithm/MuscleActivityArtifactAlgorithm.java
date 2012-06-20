package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import org.signalml.plugin.newartifact.data.NewArtifactConstants;

public class MuscleActivityArtifactAlgorithm extends NewArtifactAlgorithmBase {

	private final double weights[];
	private final FFTHelper fftHelper;

	public MuscleActivityArtifactAlgorithm(NewArtifactConstants constants) {
		super(constants);

		int smallBlockLength = this.constants.getSmallBlockLength();
		this.weights = new double[smallBlockLength];
		for (int j = 1; j < smallBlockLength + 1; ++j) {
			this.weights[j - 1] = (1.0d - Math.cos(2.0d * j * Math.PI
												   / (smallBlockLength + 1))) / 2.0d;
		}

		this.fftHelper = new FFTHelper(this.constants.getSmallBlockLength(),
									   2 * this.constants.getSmallBlockPowerVectorLength());

		this.resultBuffer = new double[2][this.constants.channelCount
										  * this.constants.getBlockCapacity()];
	}

	@Override
	public double[][] computeHead(NewArtifactAlgorithmData data) {
		return this.zeros(2, this.constants.channelCount
						  * this.constants.getBlockCapacity());
	}

	@Override
	public double[][] computeTail(NewArtifactAlgorithmData data) {
		return this.computeHead(data);
	}

	@Override
	public double[][] compute(NewArtifactAlgorithmData data) {
		double signal[][] = data.signal;
		int channelCount = this.constants.channelCount;

		double subSignal[][] = new double[signal.length][];
		int paddingLeft = this.constants.getPaddingLength();
		int smallBlockLength = this.constants.getSmallBlockLength();

		double coefficient = this.constants
							 .getFreqChangeCoefficientForSmallBlock();
		int lowMuscleStart = (int)(this.constants.getFm1() * coefficient);
		int lowMuscleEnd = (int)(this.constants.getFs1() * coefficient) - 1;
		int highMuscleStart = (int)(this.constants.getFs2() * coefficient);
		int highMuscleEnd = (int)(this.constants.getFm2() * coefficient);

		for (int k = 0; k < this.constants.getBlockCapacity(); ++k) {
			for (int i = 0; i < channelCount; ++i) {
				int start = paddingLeft + k * smallBlockLength;
				double v[] = subSignal[i] = Arrays.copyOfRange(signal[i],
											start, start + smallBlockLength);
				for (int j = 0; j < smallBlockLength; ++j) {
					v[j] *= this.weights[j];
				}
			}

			DetrendHelper.detrend(subSignal);

			double y[] = new double[2 * smallBlockLength];

			for (int i = 0; i < channelCount; ++i) {
				double u[] = subSignal[i];
				double sumL, sumML, sumMH, sumP, sumAll;
				sumL = sumML = sumMH = sumP = 0.0d;

				this.fftHelper.fft(u, y);

				int j;
				for (j = 0; j < 2 * lowMuscleStart; j += 2) {
					sumL += y[j];
				}
				for (; j <= 2 * lowMuscleEnd; j += 2) {
					sumML += y[j];
				}
				for (; j <= 2 * highMuscleStart; j += 2) {
					sumP += y[j];
				}
				for (; j <= 2 * highMuscleEnd; j += 2) {
					sumMH += y[j];
				}

				sumAll = sumL + sumML + sumMH
						 + MuscleActivityArtifactAlgorithm.DELTA;

				this.resultBuffer[0][i + k * channelCount] = (sumML + sumMH) / sumAll;
				this.resultBuffer[1][i + k * channelCount] = sumP / (sumAll + sumP);
			}
		}

		return this.resultBuffer;
	}
}
