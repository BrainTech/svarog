package org.signalml.plugin.newartifact.logic.tag;

import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.logic.tag.creators.EyeMovementTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.BreathingTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.ECGTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.EyeBlinksTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.INewArtifactTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.MuscleTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.PowerTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.TechnicalTagCreator;
import org.signalml.plugin.newartifact.logic.tag.creators.UnknownTagCreator;


public class NewArtifactTagCreatorFactory {
	public INewArtifactTagCreator createTagger(NewArtifactComputationType taggerType) {
		switch (taggerType) {
		case GALV:
			return new BreathingTagCreator();
		case EYE_MOVEMENT:
			return new EyeMovementTagCreator();
		case ECG:
			return new ECGTagCreator();
		case EYEBLINKS:
			return new EyeBlinksTagCreator();
		case MUSCLE_ACTIVITY:
			return new MuscleTagCreator();
		case POWER:
			return new PowerTagCreator();
		case TECHNICAL:
			return new TechnicalTagCreator();
		case UNKNOWN:
			return new UnknownTagCreator();
		default:
			return null;
		}
	}
}
