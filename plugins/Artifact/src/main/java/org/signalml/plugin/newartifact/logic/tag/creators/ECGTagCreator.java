package org.signalml.plugin.newartifact.logic.tag.creators;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class ECGTagCreator extends AbstractNewArtifactTagCreator implements
	INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.ECG;

	private static final double TRESHOLD_A = 0.99;
	private static final double TRESHOLD_B = 0.8;

	@Override
	protected String getTagName() {
		return "E";
	}

	@Override
	protected String getTagDescription() {
		return "EKG";
	}

	@Override
	protected int getTagStretch() {
		return 4;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		double sensitivity = data.parameters.getSensitivity(ECGTagCreator.CREATOR_TYPE) / 100.0;
		final double treshold = ECGTagCreator.TRESHOLD_A + sensitivity
					* (ECGTagCreator.TRESHOLD_B - ECGTagCreator.TRESHOLD_A);

		boolean exclusion[] = this.getExclusionMatrix(data);
		double tresholdMatrix[] = this.getTresholdMatrix(data, exclusion, treshold);

		return this.constructResult(this.createTagsUsingTreshold(data, tresholdMatrix, exclusion));
	}

	protected double[] getTresholdMatrix(NewArtifactTagData data,
					     boolean exclusion[], double baseTreshold) {
		int eegChannels[] = data.eegChannels;
		double tresholdMatrix[] = new double[eegChannels.length];
		for (int i = 0; i < eegChannels.length; ++i) {
			tresholdMatrix[i] = exclusion[eegChannels[i]] ? 0 : baseTreshold;
		}

		return tresholdMatrix;
	}
}
