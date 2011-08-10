package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;
import org.signalml.plugin.newartifact.logic.tag.NewArtifactVerticalMaxHelper;

public class EyeMovementTagCreator extends AbstractNewArtifactTagCreator
	implements INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.EYE_MOVEMENT;

	private static final double FACTOR_A = 0.0;
	private static final double FACTOR_B = 0.5;

	private static final double TRESHOLD_A = -0.97;
	private static final double TRESHOLD_B = 0.1;

	private static final double TRESHOLD_ABS_A = 0.99;
	private static final double TRESHOLD_ABS_B = 0.5;

	@Override
	protected String getTagName() {
		return "R";
	}

	@Override
	protected String getTagDescription() {
		return "Ruchy galek ocznych";
	}

	@Override
	protected int getTagStretch() {
		return 4;
	}


	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
					   .getSensitivity(EyeMovementTagCreator.CREATOR_TYPE) / 100.0;
		final double factor = EyeMovementTagCreator.FACTOR_A + sensitivity
				      * (EyeMovementTagCreator.FACTOR_B - EyeMovementTagCreator.FACTOR_A);
		final double treshold = EyeMovementTagCreator.TRESHOLD_A + sensitivity
					* (EyeMovementTagCreator.TRESHOLD_B - EyeMovementTagCreator.TRESHOLD_A);
		final double absTreshold = EyeMovementTagCreator.TRESHOLD_ABS_A + sensitivity
					   * (EyeMovementTagCreator.TRESHOLD_ABS_B - EyeMovementTagCreator.TRESHOLD_ABS_A);

		List<Integer> tags = new LinkedList<Integer>();
		double source[][] = data.source;
		int blockCount = source[0].length >> 2;

		int prevIdx = -1;
		for (int i = 0; i < blockCount; ++i) {
			int idx = this.findLocalMin(source, (i << 2) + 3, 0, 3);

			if (this.checkTagCondition(source, idx, i, treshold, absTreshold)) {
				tags.add(i);

				if (prevIdx != -1 && i > 1 && this.checkTagCondition(source, prevIdx, i - 1,
						treshold + factor, absTreshold)) {
					tags.add(i - 1);
				}

				if (i < blockCount - 1) {
					if (this.checkTagCondition(source,
								   this.findLocalMin(source, ((i + 1) << 2) + 3, 0, 3),
								   i + 1, treshold + factor, absTreshold)) {
						tags.add(i + 1);
					}
				}
			}

			prevIdx = idx;
		}

		return this.constructResult(tags);
	}

	private int findLocalMin(double source[][], int i, int begin, int end) {
		double parameterMin = Double.POSITIVE_INFINITY;
		int idx = 0;
		for (int j = begin; j < end; ++j) {
			double value = source[j][i];
			if (value < parameterMin) {
				parameterMin = value;
				idx = j;
			}
		}
		return idx;
	}

	private boolean checkTagCondition(double source[][], int channel, int block,
					  double treshold, double absTreshold) {
		double parameterMin = source[channel][(block << 2) + 3];
		return (parameterMin < treshold &&
			NewArtifactVerticalMaxHelper.GetVMax(source, channel + (block << 2)) > absTreshold);
	}

}
