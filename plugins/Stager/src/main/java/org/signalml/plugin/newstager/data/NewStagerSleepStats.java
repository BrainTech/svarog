package org.signalml.plugin.newstager.data;

public class NewStagerSleepStats {

	public final double alphaThresholds;
	public final double deltaThreshold;
	public final double spindleThreshold;
	public final double toneMThreshold;

	public NewStagerSleepStats(double alphaThresholds, double deltaThreshold,
			double spindleThreshold, double toneMThreshold) {
		this.alphaThresholds = alphaThresholds;
		this.deltaThreshold = deltaThreshold;
		this.spindleThreshold = spindleThreshold;
		this.toneMThreshold = toneMThreshold;
	}

}
