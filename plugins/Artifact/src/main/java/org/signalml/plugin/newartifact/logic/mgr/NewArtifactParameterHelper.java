package org.signalml.plugin.newartifact.logic.mgr;

import java.util.Map;

import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.data.NewArtifactData;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;

public class NewArtifactParameterHelper {
	public static boolean IsParameterEnabled(
		NewArtifactComputationType algorithmType,
		NewArtifactData artifactData) {

		NewArtifactParameters parameters = artifactData.getParameters();
		Map<String, Integer> channelMap = artifactData.getChannelMap();

		int idx = -1;
		int chosenTypes[] = parameters.getChosenArtifactTypes();

		boolean channelsEnabled = true;

		switch (algorithmType) {
		case GALV:
			idx = 0;
			break;
		case EYE_MOVEMENT:
			channelsEnabled = NewArtifactParameterHelper
							  .CheckChannelAvailability(channelMap, "EOGL", "EOGP", "F7",
									  "F8", "T3", "T4");
			idx = 1;
			break;
		case MUSCLE_ACTIVITY:
			idx = 2;
			break;
		case MUSCLE_PLUS_POWER:
			return chosenTypes[2] != 0 || chosenTypes[5] != 0;
		case EYEBLINKS:
			channelsEnabled = NewArtifactParameterHelper
							  .CheckChannelAvailability(channelMap, "Fp1", "F3", "F3",
									  "C3", "Fp2", "F4", "F4", "C4");
			idx = 3;
			break;
		case TECHNICAL:
			idx = 4;
			break;
		case POWER:
			idx = 5;
			break;
		case ECG:
			channelsEnabled = NewArtifactParameterHelper
							  .CheckChannelAvailability(channelMap, "ECG");
			idx = 6;
			break;
		case UNKNOWN:
			idx = 7;
			break;
		default:
			return false;
		}
		return channelsEnabled && (idx == -1 || chosenTypes[idx] != 0);
	}

	private static boolean CheckChannelAvailability(
		Map<String, Integer> channelMap, String... channelNames) {
		for (String channelName : channelNames) {
			if (channelMap.get(channelName) == null) {
				return false;
			}
		}

		return true;
	}
}
