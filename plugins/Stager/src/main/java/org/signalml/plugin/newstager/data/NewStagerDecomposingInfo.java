package org.signalml.plugin.newstager.data;

public class NewStagerDecomposingInfo {

	public final float energyPercent;
	public final int numberOfIterations;
	public final int sizeOfDictionary;
	public final char typeOfDictionary;

	public NewStagerDecomposingInfo(float energyPercent, int numberOfIterations, int sizeOfDictionary, char typeOfDictionary) {
		this.energyPercent = energyPercent;
		this.numberOfIterations = numberOfIterations;
		this.sizeOfDictionary = sizeOfDictionary;
		this.typeOfDictionary = typeOfDictionary;
	}

}
