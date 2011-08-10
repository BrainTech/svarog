package org.signalml.plugin.newartifact.logic.tag;

public class NewArtifactVerticalMaxHelper {
	public static double GetVMax(double source[][], int column) {
		double retVal = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < source.length; ++i) {
			retVal = Math.max(retVal, source[i][column]);
		}
		return retVal;
	}
}
