package org.signalml.plugin.newstager.data;

public class NewStagerSignalStatsResult extends NewStagerResult { //TODO this should not extend NewStagerResult

	public final NewStagerSleepStats signalStatCoeffs;
	public final NewStagerParameters newParameters;
	public final double muscle[];
	public final boolean montage[];

	public NewStagerSignalStatsResult(NewStagerSleepStats signalStatCoeffs,
			NewStagerParameters newParameters, double muscle[],
			boolean montage[]) {
		this.signalStatCoeffs = signalStatCoeffs;
		this.newParameters = newParameters;
		this.muscle = muscle;
		this.montage = montage;
	}

}
