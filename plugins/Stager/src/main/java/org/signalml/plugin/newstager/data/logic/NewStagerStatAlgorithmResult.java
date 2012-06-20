package org.signalml.plugin.newstager.data.logic;

public class NewStagerStatAlgorithmResult {

	public final double deviation;
	public final double muscle[];
	public final boolean montage[];

	public NewStagerStatAlgorithmResult(double deviation, double muscle[],
										boolean montage[]) {
		this.deviation = deviation;
		this.muscle = muscle;
		this.montage = montage;
	}
}
