package org.signalml.plugin.newstager.data;

public class NewStagerFixedParameters {

	public final double swaWidthCoeff;
	public final double alphaPerc1;
	public final double alphaPerc2;
	public final double corrCoeffRems;
	public final double corrCoeffSems;

	public NewStagerFixedParameters(double swaWidthCoeff, double alphaPerc1,
									double alphaPerc2, double corrCoeffRems, double corrCoeffSems) {
		this.swaWidthCoeff = swaWidthCoeff;
		this.alphaPerc1 = alphaPerc1;
		this.alphaPerc2 = alphaPerc2;
		this.corrCoeffRems = corrCoeffRems;
		this.corrCoeffSems = corrCoeffSems;
	}

}