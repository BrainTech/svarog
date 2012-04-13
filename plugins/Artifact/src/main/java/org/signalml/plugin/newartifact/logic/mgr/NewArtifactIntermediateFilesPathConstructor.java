package org.signalml.plugin.newartifact.logic.mgr;

import java.io.File;

import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.data.NewArtifactData;

public class NewArtifactIntermediateFilesPathConstructor implements
	INewArtifactPathConstructor {

	private final NewArtifactData artifactData;

	public NewArtifactIntermediateFilesPathConstructor(
		final NewArtifactData artifactData) {
		this.artifactData = artifactData;
	}

	@Override
	public String[] getIntermediateFileNamesForAlgorithm(
		NewArtifactComputationType algorithmType) {

		switch (algorithmType) {
		case GALV:
			return new String[] { "moj_par_oddech_4s.bin" };
		case MUSCLE_PLUS_POWER:
			return new String[] { "moj_par_muscle_1s.bin",
								  "moj_par_powerf_1s.bin"
								};
		case MUSCLE_ACTIVITY:
			return new String[] { "moj_par_muscle_1s.bin" };
		case EYE_MOVEMENT:
			return new String[] { "moj_par_correl_4s.bin" };
		case POWER:
			return new String[] { "moj_par_powerf_1s.bin" };
		case TECHNICAL:
			return new String[] { "moj_par_xxapar_4s.bin" };
		case ECG:
			return new String[] { "moj_par_corEKG_4s.bin" };
		case EYEBLINKS:
			return new String[] { "moj_par_omruga_1s.bin" };
		case UNKNOWN:
			return new String[] { "moj_par_el_pop_4s.bin" };
		default:
			return null;
		}
	}

	@Override
	public String getPathToWorkDir() {
		return new File(this.artifactData.getProjectPath(), artifactData
						.getPatientName()).getAbsolutePath();
	}

	@Override
	public String getTagFileExtension() {
		return ".tag";
	}

}
