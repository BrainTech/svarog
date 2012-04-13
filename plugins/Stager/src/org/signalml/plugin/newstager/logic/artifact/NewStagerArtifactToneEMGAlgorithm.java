package org.signalml.plugin.newstager.logic.artifact;

import java.util.Arrays;

import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.plugin.newstager.data.NewStagerArtifactAlgorithmData;
import org.signalml.plugin.newstager.exception.NewStagerPluginException;

public class NewStagerArtifactToneEMGAlgorithm extends
	NewStagerArtifactAnalysisAlgorithmBase {

	private final double percents[] = { 10.0d, 90.0d };

	private NewStagerArtifactAlgorithmData data;

	public NewStagerArtifactToneEMGAlgorithm(NewStagerArtifactAlgorithmData data) {
		this.data = data;
	}

	public double run(double signal[][]) {
		double channelSignal[];

		try {
			channelSignal = GetChannelSignal(this.data.channels, EegChannel.EMG, signal);
		} catch (NewStagerPluginException e) {
			return 0.0d;
		}

		double mean[] = this.computeItermediateMeanFiltered(channelSignal,
						NewStagerArtifactAnalysisAlgorithmBase.butterEMGNum,
						NewStagerArtifactAnalysisAlgorithmBase.butterEMGDen,
						this.data.constants);

		if (mean == null) {
			return 0.0d;
		}

		Arrays.sort(mean);
		double percentiles[] = this.percentile(mean, this.percents);
		double lowP = percentiles[0];
		double highP = percentiles[1];

		double sum = 0.0d;
		int count = 0;
		for (int i = 0; i < mean.length; ++i) {
			double value = mean[i];
			if (value >= lowP && value <= highP) {
				sum += value;
				++count;
			}
		}

		if (count <= 0) {
			return 0.0d;
		}

		return sum / count;
	}

	public double[] percentile(double source[], double percentiles[]) {
		double result[] = new double[percentiles.length];
		double percents[] = new double[source.length];

		for (int i = 0; i < source.length; ++i) {
			percents[i] = 100.0d / source.length * ((double)(i + 1) - 0.5d);
		}

		for (int p = 0; p < percentiles.length; ++p) {
			int k = Arrays.binarySearch(percents, percentiles[p]);
			if (k < 0) {
				k = -k - 1;
			}
			k = Math.max(k, 1);
			result[p] = source[k - 1] + source.length
						* (percentiles[p] - percents[k - 1]) / 100.d
						* (source[k] - source[k - 1]);
		}

		return result;
	}

	public static void main(String[] args) {
		NewStagerArtifactToneEMGAlgorithm a = new NewStagerArtifactToneEMGAlgorithm(null);
		double[] re = a.percentile(new double[] { 15, 20, 35, 40, 50 },
								   new double[] { 10, 30, 90 });
		for (int i = 0; i < re.length; i++) {
			System.out.println(re[i]);
		}
	}
}
