package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.plugin.newstager.helper.NewStagerConfigurationDefaultsHelper;

public class NewStagerParameterThresholds implements Serializable {

	private static final long serialVersionUID = -5779920969012386681L;

	public double toneEMG;
	public double montageEEGThreshold;
	public double montageEMGThreshold;
	public double montageToneEMGThreshold;

	public double remEogDeflectionThreshold;
	public double semEogDeflectionThreshold;

	public final NewStagerFASPThreshold alphaThreshold;
	public final NewStagerFASPThreshold deltaThreshold;
	public final NewStagerFASPThreshold spindleThreshold;
	public final NewStagerFASPThreshold thetaThreshold;
	public final NewStagerFASPThreshold kCThreshold;

	public NewStagerParameterThresholds() {
		this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				NewStagerParameterThresholds.Zeros(),
				NewStagerParameterThresholds.Zeros(),
				NewStagerParameterThresholds.Zeros(),
				NewStagerParameterThresholds.Zeros(),
				NewStagerParameterThresholds.Zeros());
		NewStagerConfigurationDefaultsHelper.GetSharedInstance().setDefaults(
				this);
	}

	public NewStagerParameterThresholds(double toneEMG,
			double montageEEGThreshold, double montageEMGThreshold,
			double montageToneEMGThreshold, double remEogDeflectionThreshold,
			double semEogDeflectionThreshold,
			NewStagerFASPThreshold alphaThreshold,
			NewStagerFASPThreshold deltaThreshold,
			NewStagerFASPThreshold spindleThreshold,
			NewStagerFASPThreshold thetaThreshold,
			NewStagerFASPThreshold KCThreshold) {
		this.toneEMG = toneEMG;
		this.montageEEGThreshold = montageEEGThreshold;
		this.montageEMGThreshold = montageEMGThreshold;
		this.montageToneEMGThreshold = montageToneEMGThreshold;
		this.remEogDeflectionThreshold = remEogDeflectionThreshold;
		this.semEogDeflectionThreshold = semEogDeflectionThreshold;
		this.alphaThreshold = alphaThreshold;
		this.deltaThreshold = deltaThreshold;
		this.spindleThreshold = spindleThreshold;
		this.thetaThreshold = thetaThreshold;
		this.kCThreshold = KCThreshold;
	}

	private static NewStagerFASPThreshold Zeros() {
		return NewStagerFASPThreshold.CreateThreshold(0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0);
	}

}
