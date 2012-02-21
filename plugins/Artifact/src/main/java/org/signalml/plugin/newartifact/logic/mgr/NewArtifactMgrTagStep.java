package org.signalml.plugin.newartifact.logic.mgr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.io.PluginTagWriterConfig;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.io.IPluginTagWriter;
import org.signalml.plugin.io.PluginTagWriter;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.method.logic.IPluginComputationMgrStepTrackerProxy;
import org.signalml.plugin.newartifact.data.NewArtifactComputationType;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepData;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrStepResult;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagRoutineData;
import org.signalml.plugin.newartifact.io.INewArtifactDataReader;
import org.signalml.plugin.newartifact.io.NewArtifactDataReader;
import org.signalml.plugin.newartifact.logic.tag.NewArtifactTagCreatorFactory;
import org.signalml.plugin.newartifact.logic.tag.NewArtifactTagCreatorRoutine;
import org.signalml.plugin.newartifact.logic.tag.NewArtifactTagMerger;

public class NewArtifactMgrTagStep extends
		AbstractPluginComputationMgrStep<NewArtifactMgrStepData> {

	private final ExecutorCompletionService<NewArtifactTagResult> executorService;
	private final Map<NewArtifactComputationType, NewArtifactTagCreatorRoutine> taggerRoutines;
	private final Collection<Future<NewArtifactTagResult>> futureTasks;
	private Map<NewArtifactComputationType, INewArtifactDataReader> readers;

	public NewArtifactMgrTagStep(NewArtifactMgrStepData data) {
		super(data);

		this.taggerRoutines = new HashMap<NewArtifactComputationType, NewArtifactTagCreatorRoutine>();
		this.futureTasks = new LinkedList<Future<NewArtifactTagResult>>();
		this.executorService = new ExecutorCompletionService<NewArtifactTagResult>(
				Executors.newCachedThreadPool(data.threadFactory));

		this.readers = null;
	}

	@Override
	public int getStepNumberEstimate() {
		this.prepareReadersIfNeeded();

		int ticks = 0;
		for (INewArtifactDataReader reader : this.readers.values()) {
			try {
				ticks += reader.getDataSize();
			} catch (IOException e) {
				continue;
			}
		}
		return ticks / this.data.constants.getBlockLength();
	}

	@Override
	public PluginComputationMgrStepResult doRun(
			PluginComputationMgrStepResult prevStepResult)
			throws PluginToolAbortException, ComputationException,
			PluginToolInterruptedException {

		final IPluginComputationMgrStepTrackerProxy<NewArtifactProgressPhase> tracker = this.data.tracker;

		tracker.setProgressPhase(NewArtifactProgressPhase.TAGGER_PREPARE_PHASE);

		this.checkAbortState();
		this.prepareWorkers();

		Map<Future<NewArtifactTagResult>, NewArtifactComputationType> futureMap = new HashMap<Future<NewArtifactTagResult>, NewArtifactComputationType>();
		Future<NewArtifactTagResult> future = null;

		this.checkAbortState();
		for (Entry<NewArtifactComputationType, NewArtifactTagCreatorRoutine> entry : this.taggerRoutines
				.entrySet()) {
			future = this.executorService.submit(entry.getValue());
			futureMap.put(future, entry.getKey());
			this.futureTasks.add(future);
		}

		NewArtifactTagMerger merger = new NewArtifactTagMerger();

		tracker.setProgressPhase(NewArtifactProgressPhase.TAGGING_PHASE);
		for (int j = 0; j < this.taggerRoutines.size(); ++j) {
			this.checkAbortState();
			try {
				future = this.executorService.take();
				if (future != null) {
					NewArtifactTagResult result = future.get();
					merger.addTag(result);

					NewArtifactComputationType taggerType = futureMap
							.get(future);
					if (taggerType != null) {
						tracker.advance((int) this.readers.get(taggerType)
								.getDataSize()
								/ this.data.constants.getBlockLength());
					}
				}
			} catch (InterruptedException e) {
				throw new PluginToolInterruptedException(e);
			} catch (ExecutionException e) {
				throw new ComputationException(e);
			} catch (IOException e) {
				throw new ComputationException(e);
			}
		}

		this.checkAbortState();
		tracker.setProgressPhase(NewArtifactProgressPhase.TAG_MERGING_PHASE);
		return this.mergeTags(merger);
	}

	@Override
	protected NewArtifactMgrStepResult prepareStepResult() {
		return new NewArtifactMgrStepResult(this.getClass());
	}

	@Override
	protected void cleanup() {
		for (Future<NewArtifactTagResult> future : this.futureTasks) {
			future.cancel(true);
		}
		this.futureTasks.clear();

		if (this.readers != null) {
			this.readers.clear();
			this.readers = null;
		}
	}

	private void prepareWorkers() {
		this.prepareReadersIfNeeded();

		NewArtifactTagCreatorFactory factory = new NewArtifactTagCreatorFactory();

		Collection<Integer> channelsList = this.data.artifactData
				.getEegChannels();
		int eegChannels[] = new int[channelsList.size()];
		int i = 0;
		for (int channel : channelsList) {
			eegChannels[i] = channel;
			i++;
		}

		for (NewArtifactComputationType taggerType : NewArtifactComputationType
				.values()) {
			if (!NewArtifactParameterHelper.IsParameterEnabled(taggerType,
					this.data.artifactData)) {
				continue;
			}

			INewArtifactDataReader reader = this.readers.get(taggerType);
			IPluginTagWriter writer = this.createTagWriterForTagger(taggerType,
					this.data);

			if (reader != null && writer != null) {
				this.taggerRoutines
						.put(taggerType,
								new NewArtifactTagCreatorRoutine(
										new NewArtifactTagRoutineData(
												this.data.constants,
												this.data.artifactData
														.getParameters(),
												eegChannels,
												this.getExcludedChannelsForTagger(
														taggerType, this.data)),
										reader, factory
												.createTagger(taggerType),
										writer));
			}
		}

	}

	private NewArtifactMgrStepResult mergeTags(NewArtifactTagMerger merger)
			throws ComputationException {
		File targetFile = new File(
				this.data.pathConstructor.getPathToWorkDir(),
				this.data.artifactData.getPatientName()
						+ this.data.pathConstructor.getTagFileExtension());
		PluginTagWriter writer = new PluginTagWriter(targetFile,
				new PluginTagWriterConfig());
		try {
			writer.writeTags(Arrays.asList(merger.merge().tagGroup));
		} catch (IOException e) {
			throw new ComputationException(e);
		} catch (SignalMLException e) {
			throw new ComputationException(e);
		}

		NewArtifactMgrStepResult result = this.prepareStepResult();
		result.resultTagPath = targetFile.getAbsolutePath();
		return result;
	}

	private void prepareReadersIfNeeded() {
		if (this.readers == null) {
			this.readers = new HashMap<NewArtifactComputationType, INewArtifactDataReader>();

			for (NewArtifactComputationType taggerType : NewArtifactComputationType
					.values()) {
				if (NewArtifactParameterHelper.IsParameterEnabled(taggerType,
						this.data.artifactData)) {
					INewArtifactDataReader reader = this
							.createDataReaderForTagger(taggerType, this.data);
					if (reader != null) {
						this.readers.put(taggerType, reader);
					}
				}
			}
		}
	}

	private IPluginTagWriter createTagWriterForTagger(
			NewArtifactComputationType taggerType, NewArtifactMgrStepData data) {
		switch (taggerType) {
		case MUSCLE_PLUS_POWER:
			return null;
		default:
			return new PluginTagWriter(new File(
					data.pathConstructor.getPathToWorkDir(),
					this.getResultFileNameForAlgorithm(taggerType)[0]),
					new PluginTagWriterConfig());
		}
	}

	private INewArtifactDataReader createDataReaderForTagger(
			NewArtifactComputationType taggerType, NewArtifactMgrStepData data) {
		int channelCount = this.data.constants.channelCount;
		switch (taggerType) {
		case MUSCLE_PLUS_POWER:
			return null;
		case EYEBLINKS:
			channelCount = 2;
		default:
			return new NewArtifactDataReader(
					new File(
							data.pathConstructor.getPathToWorkDir(),
							data.pathConstructor
									.getIntermediateFileNamesForAlgorithm(taggerType)[0]),
					channelCount);
		}
	}

	private int[] getExcludedChannelsForTagger(
			NewArtifactComputationType taggerType, NewArtifactMgrStepData data) {
		int idx;
		switch (taggerType) {
		case GALV:
			idx = 0;
			break;
		case MUSCLE_ACTIVITY:
			idx = 2;
			break;
		case TECHNICAL:
			idx = 4;
			break;
		case POWER:
			idx = 5;
			break;
		case ECG:
			idx = 6;
			break;
		case UNKNOWN:
			idx = 7;
			break;
		case EYEBLINKS:
		case MUSCLE_PLUS_POWER:
		case EYE_MOVEMENT:
		default:
			return null;
		}

		return data.artifactData.getExcludedChannels()[idx];
	}

	private String[] getResultFileNameForAlgorithm(
			NewArtifactComputationType taggerType) {
		String result[] = this.doGetResultFileNameForAlgorithm(taggerType);
		if (result == null) {
			return result;
		}

		for (int i = 0; i < result.length; ++i) {
			result[i] = result[i]
					+ this.data.pathConstructor.getTagFileExtension();
		}
		return result;
	}

	private String[] doGetResultFileNameForAlgorithm(
			NewArtifactComputationType taggerType) {
		switch (taggerType) {
		case GALV:
			return new String[] { "galw_4s" };
		case MUSCLE_ACTIVITY:
			return new String[] { "mies_4s" };
		case POWER:
			return new String[] { "siec_1s" };
		case EYE_MOVEMENT:
			return new String[] { "rugo_4s" };
		case ECG:
			return new String[] { "EKG__4s" };
		case EYEBLINKS:
			return new String[] { "mrug_1s" };
		case TECHNICAL:
			return new String[] { "apar_4s" };
		case UNKNOWN:
			return new String[] { "elpo_4s" };
		case MUSCLE_PLUS_POWER:
		default:
			return null;
		}
	}
}
