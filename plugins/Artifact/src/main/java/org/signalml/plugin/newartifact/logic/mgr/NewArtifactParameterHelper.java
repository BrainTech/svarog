package org.signalml.plugin.newartifact.logic.mgr;

import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;

public class NewArtifactParameterHelper {
	public static boolean IsParameterEnabled(
		NewArtifactComputationType algorithmType,
		NewArtifactParameters parameters) {
		int idx = -1;
		int[] chosenTypes = parameters.getChosenArtifactTypes();

		switch (algorithmType) {
		case GALV:
			idx = 0;
			break;
		case EYE_MOVEMENT:
			idx = 1;
			break;
		case MUSCLE_ACTIVITY:
			idx = 2;
			break;
		case MUSCLE_PLUS_POWER:
			return chosenTypes[2] != 0 || chosenTypes[5] != 0;
		case EYEBLINKS:
			idx = 3;
			break;
		case TECHNICAL:
			idx = 4;
			break;
		case POWER:
			idx = 5;
			break;
		case ECG:
			idx = 6;
			break;
		case UNKNOWN:
			idx = 7;
			break;
		default:
			return false;
		}
		return idx == -1 || chosenTypes[idx] != 0;
	}
}
