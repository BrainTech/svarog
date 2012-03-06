package org.signalml.plugin.newstager.data;

public class NewStagerParameterThresholds {
	public final double toneEMG;
	public final double montageEEGThreshold;
	public final double montageEMGThreshold;
	public final Double montageToneEMGThreshold;
	public final NewStagerFASPThreshold alphaThreshold;
	public final NewStagerFASPThreshold deltaThreshold;
	public final NewStagerFASPThreshold spindleThreshold;
	public final NewStagerFASPThreshold thetaThreshold;
	public final NewStagerFASPThreshold kCThreshold;

	public NewStagerParameterThresholds(
			double toneEMG,
			double montageEEGThreshold,
			double montageEMGThreshold, double montageToneEMGThreshold,
			NewStagerFASPThreshold alphaThreshold,
			NewStagerFASPThreshold deltaThreshold,
			NewStagerFASPThreshold spindleThreshold,
			NewStagerFASPThreshold thetaThreshold,
			NewStagerFASPThreshold KCThreshold) {
		this.toneEMG = toneEMG;
		this.montageEEGThreshold = montageEEGThreshold;
		this.montageEMGThreshold = montageEMGThreshold;
		this.montageToneEMGThreshold = montageToneEMGThreshold;
		this.alphaThreshold = alphaThreshold;
		this.deltaThreshold = deltaThreshold;
		this.spindleThreshold = spindleThreshold;
		this.thetaThreshold = thetaThreshold;
		this.kCThreshold = KCThreshold;
	}
}
