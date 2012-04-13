package org.signalml.plugin.newstager.logic.artifact;

import java.util.LinkedList;
import java.util.List;

import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.plugin.newstager.data.NewStagerArtifactAlgorithmData;
import org.signalml.plugin.newstager.data.logic.NewStagerStatAlgorithmResult;
import org.signalml.plugin.newstager.data.logic.NewStagerStatData;
import org.signalml.plugin.newstager.exception.NewStagerPluginException;

public class NewStagerStatAlgorithm {

	private final NewStagerArtifactMontageAlgorithm montageAnalyser;
	private final NewStagerArtifactToneEMGAlgorithm emgAnalyser;

	private List<Boolean> montageArtifacts;
	private List<Double> emgArtifacts;

	private double c3a2diff;
	private double c3a2diffSquare;

	private NewStagerStatData data;

	public NewStagerStatAlgorithm(NewStagerStatData data) {
		this.data = data;

		NewStagerArtifactAlgorithmData algorithmData = new NewStagerArtifactAlgorithmData(
			data.channels, data.constants, data.parameters);

		this.montageAnalyser = new NewStagerArtifactMontageAlgorithm(
			algorithmData);
		this.emgAnalyser = new NewStagerArtifactToneEMGAlgorithm(algorithmData);

		this.montageArtifacts = new LinkedList<Boolean>();
		this.emgArtifacts = new LinkedList<Double>();

		this.c3a2diff = 0.0d;
		this.c3a2diffSquare = 0.0d;
	}

	public void compute(double signal[][]) throws NewStagerPluginException {
		this.montageArtifacts.add(this.montageAnalyser.run(signal));
		this.emgArtifacts.add(this.emgAnalyser.run(signal));

		double c3[] = NewStagerArtifactAnalysisAlgorithmBase.GetChannelSignal(
						  this.data.channels, EegChannel.C3, signal);
		double a2[] = NewStagerArtifactAnalysisAlgorithmBase.GetChannelSignal(
						  this.data.channels, EegChannel.A2, signal);

		assert(c3.length == a2.length);
		for (int i = 0; i < c3.length; ++i) {
			double diff = c3[i] - a2[i];
			this.c3a2diff += diff;
			this.c3a2diffSquare += diff * diff;
		}
	}

	public NewStagerStatAlgorithmResult getResult() {
		int length = this.emgArtifacts.size();
		assert(length == this.montageArtifacts.size());
		double muscle[] = new double[length];
		boolean montage[] = new boolean[length];

		int i = 0;
		for (Double v : this.emgArtifacts) {
			muscle[i] = v;
			++i;
		}

		i = 0;
		for (Boolean v : this.montageArtifacts) {
			montage[i] = v;
			++i;
		}

		int count = length * this.data.constants.getBlockLength();
		double mean = this.c3a2diff / count;
		double dev = this.c3a2diffSquare - 2 * mean * this.c3a2diff + mean
					 * mean * count;

		return new NewStagerStatAlgorithmResult(Math.sqrt(dev / (count - 1)),
												muscle, montage);
	}
}
