package org.signalml.plugin.newartifact.logic.tag.creators;

import java.util.Collection;
import java.util.TreeSet;

import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class UnknownTagCreator extends AbstractTresholdTagCreator implements
	INewArtifactTagCreator {

	private static final NewArtifactType CREATOR_TYPE = NewArtifactType.UNKNOWN;

	private static final double TRESHOLD_A = 800.0;
	private static final double TRESHOLD_B = 0.0;


	@Override
	protected String getTagName() {
		return "U";
	}

	@Override
	protected String getTagDescription() {
		return "Unknown";
	}

	@Override
	protected int getTagStretch() {
		return 4;
	}

	@Override
	public NewArtifactTagResult tag(NewArtifactTagData data) {
		final double sensitivity = data.parameters
					   .getSensitivity(UnknownTagCreator.CREATOR_TYPE) / 100.0;
		double treshold = UnknownTagCreator.TRESHOLD_A + sensitivity
				  * (UnknownTagCreator.TRESHOLD_B - UnknownTagCreator.TRESHOLD_A);

		Collection<Integer> tags = this.getTagsFromTreshold(data, treshold);

		return this.constructResult(new TreeSet<Integer>(tags));

	}

}
