package org.signalml.plugin.newartifact.logic.mgr;

public enum NewArtifactProgressPhase {
	PREPROCESS_PREPARE_PHASE,
	SOURCE_FILE_INITIAL_READ_PHASE,
	INTERMEDIATE_COMPUTATION_PHASE,
	TAGGER_PREPARE_PHASE,
	TAGGING_PHASE,
	TAG_MERGING_PHASE,

	ABORT_PHASE
}
