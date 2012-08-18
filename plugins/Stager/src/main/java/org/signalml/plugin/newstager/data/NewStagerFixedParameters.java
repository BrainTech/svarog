package org.signalml.plugin.newstager.data;

import org.signalml.plugin.newstager.helper.NewStagerConfigurationDefaultsHelper;

public class NewStagerFixedParameters {

	public double widthCoeff;
	public double swaWidthCoeff;
	public double alphaPerc1;
	public double alphaPerc2;
	public double corrCoeffRems;
	public double corrCoeffSems;

	public NewStagerFixedParameters() {
		this(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d);
		NewStagerConfigurationDefaultsHelper.GetSharedInstance().setDefaults(
				this);
	}

	public NewStagerFixedParameters(double widthCoeff, double swaWidthCoeff,
			double alphaPerc1, double alphaPerc2, double corrCoeffRems,
			double corrCoeffSems) {
		this.widthCoeff = widthCoeff;
		this.swaWidthCoeff = swaWidthCoeff;
		this.alphaPerc1 = alphaPerc1;
		this.alphaPerc2 = alphaPerc2;
		this.corrCoeffRems = corrCoeffRems;
		this.corrCoeffSems = corrCoeffSems;
	}

}