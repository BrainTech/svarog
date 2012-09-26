package org.signalml.plugin.newartifact.logic.mgr;

import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.logic.algorithm.BlinkingArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.BreathingArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.ECGArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.EyeMoveArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.INewArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.MuscleActivityArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.TechnicalArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.UnknownArtifactAlgorithm;

public class NewArtifactAlgorithmFactory {

	private final NewArtifactComputationType producedAlgorithmType;
	private final NewArtifactConstants constants;

	public NewArtifactAlgorithmFactory(NewArtifactComputationType prodAlgorithmType,
									   NewArtifactConstants constants) {
		this.producedAlgorithmType = prodAlgorithmType;
		this.constants = constants;
	}

	public INewArtifactAlgorithm createAlgorithm() throws PluginException {
		switch (this.producedAlgorithmType) {
		case GALV:
			return new BreathingArtifactAlgorithm(constants);
		case EYE_MOVEMENT:
			return new EyeMoveArtifactAlgorithm(constants);
		case MUSCLE_PLUS_POWER:
			return new MuscleActivityArtifactAlgorithm(constants);
		case TECHNICAL:
			return new TechnicalArtifactAlgorithm(constants);
		case ECG:
			return new ECGArtifactAlgorithm(constants);
		case EYEBLINKS:
			return new BlinkingArtifactAlgorithm(constants);
		case UNKNOWN:
			return new UnknownArtifactAlgorithm(constants);
		default:
			throw new PluginException("Unknown algorithm");
		}
	}
}
