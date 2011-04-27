package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.Collection;
import java.util.TreeSet;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class PowerTagCreator extends AbstractTresholdTagCreator implements
	INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.POWER_SUPPLY;

	private static final double TRESHOLD_A = 0.7;
	private static final double TRESHOLD_B = 0.05;

	@Override
	protected String getTagName() {
		return "P";
	}

	@Override
	protected String getTagDescription() {
		return "Art. sieci (50Hz)";
	}

	@Override
	protected int getTagStretch() {
		return 1;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
					   .getSensitivity(PowerTagCreator.CREATOR_TYPE) / 100.0;
		double treshold = PowerTagCreator.TRESHOLD_A + sensitivity
				  * (PowerTagCreator.TRESHOLD_B - PowerTagCreator.TRESHOLD_A);

		Collection<Integer> tags = this.getTagsFromTreshold(data, treshold);

		return this.constructResult(new TreeSet<Integer>(tags));
	}

}
