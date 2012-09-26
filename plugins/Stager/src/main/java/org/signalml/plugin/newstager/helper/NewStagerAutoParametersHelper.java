package org.signalml.plugin.newstager.helper;

import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;

public class NewStagerAutoParametersHelper {

	public static double GetAutoAlphaAmplitude() {
		NewStagerFASPThreshold threshold = NewStagerFASPThreshold.CreateZeroThreshold();
		Helper().setAlphaThresholdDefaults(threshold);
		return threshold.amplitude.getMin();
	}

	public static double GetAutoDeltaAmplitude() {
		NewStagerFASPThreshold threshold = NewStagerFASPThreshold.CreateZeroThreshold();
		Helper().setDeltaThresholdDefaults(threshold);
		return threshold.amplitude.getMin();
	}

	public static double GetAutoSpindleAmplitude() {
		NewStagerFASPThreshold threshold = NewStagerFASPThreshold.CreateZeroThreshold();
		Helper().setSpindleThresholdDefaults(threshold);
		return threshold.amplitude.getMin();
	}

	private static NewStagerConfigurationDefaultsHelper Helper() {
		return NewStagerConfigurationDefaultsHelper.GetSharedInstance();
	}

}
