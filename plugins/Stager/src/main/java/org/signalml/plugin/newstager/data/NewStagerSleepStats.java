package org.signalml.plugin.newstager.data;

public class NewStagerSleepStats {

	public final double alphaThreshold;
	public final double deltaThreshold;
	public final double spindleThreshold;
	public final double toneMThreshold;

	public NewStagerSleepStats(double alphaThreshold, double deltaThreshold,
							   double spindleThreshold, double toneMThreshold) {
		this.alphaThreshold = alphaThreshold;
		this.deltaThreshold = deltaThreshold;
		this.spindleThreshold = spindleThreshold;
		this.toneMThreshold = toneMThreshold;
	}

}
