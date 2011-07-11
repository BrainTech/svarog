package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.LinkedList;
import java.util.List;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;
import org.signalml.plugin.newartifact.logic.tag.NewArtifactVerticalMaxHelper;

public class EyeBlinksTagCreator extends AbstractNewArtifactTagCreator
	implements INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.EYEBLINKS;

	private static final double FACTOR_A = 0.0;
	private static final double FACTOR_B = -0.2;

	private static final double TRESHOLD_A = 0.99;
	private static final double TRESHOLD_B = 0.75;

	@Override
	protected String getTagName() {
		return "B";
	}

	@Override
	protected String getTagDescription() {
		return "Mruganie";
	}

	@Override
	protected int getTagStretch() {
		return 1;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
					   .getSensitivity(EyeBlinksTagCreator.CREATOR_TYPE) / 100.0;
		final double factor = EyeBlinksTagCreator.FACTOR_A
				      + sensitivity
				      * (EyeBlinksTagCreator.FACTOR_B - EyeBlinksTagCreator.FACTOR_A);
		final double treshold = EyeBlinksTagCreator.TRESHOLD_A
					+ sensitivity
					* (EyeBlinksTagCreator.TRESHOLD_B - EyeBlinksTagCreator.TRESHOLD_A);
		final double absTreshold = treshold + factor;

		int blockCount = data.source[0].length;

		double channelMax[] = new double[blockCount];
		List<Integer> tags = new LinkedList<Integer>();

		for (int i = 0; i < blockCount; ++i) {
			channelMax[i] = NewArtifactVerticalMaxHelper.GetVMax(data.source, i);
		}

		for (int i = 0; i < blockCount; ++i) {
			if (channelMax[i] > treshold) {
				tags.add(i);

				if (factor < 0) {
					if (i > 0 && channelMax[i - 1] > absTreshold) {
						tags.add(i - 1);
					}

					if (i < blockCount - 1 && channelMax[i + 1] > absTreshold) {
						tags.add(i + 1);
					}
				}
			}
		}

		return this.constructResult(tags);
	}

}
