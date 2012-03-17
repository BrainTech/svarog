package org.signalml.plugin.newstager.data;

import org.signalml.plugin.newstager.data.logic.NewStagerComputationMgrStepResult;

public class NewStagerSignalStatsResult extends NewStagerComputationMgrStepResult {
	
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
