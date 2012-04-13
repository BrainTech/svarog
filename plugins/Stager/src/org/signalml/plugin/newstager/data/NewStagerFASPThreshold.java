package org.signalml.plugin.newstager.data;

import org.signalml.util.MinMaxRange;

public class NewStagerFASPThreshold {
	public final MinMaxRange amplitude;
	public final MinMaxRange frequency;
	public final MinMaxRange scale;
	public final MinMaxRange phase;

	public NewStagerFASPThreshold(MinMaxRange amplitude, MinMaxRange frequency,
								  MinMaxRange scale, MinMaxRange phase) {
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.scale = scale;
		this.phase = phase;
	}

	public static NewStagerFASPThreshold CreateThreshold(Double amplitudeMin,
			Double amplitudeMax, Double frequencyMin, Double frequencyMax,
			Double scaleMin, Double scaleMax, Double phaseMin, Double phaseMax) {
		return new NewStagerFASPThreshold(CreateMinMax(amplitudeMin,
										  amplitudeMax), CreateMinMax(frequencyMin, frequencyMax),
										  CreateMinMax(scaleMin, scaleMax), CreateMinMax(phaseMin,
												  phaseMax));
	}

	private static MinMaxRange CreateMinMax(Double min, Double max) {
		return min != null || max != null ? new MinMaxRange(-99.0d, min, max,
				false, false) : null;
	}
}
