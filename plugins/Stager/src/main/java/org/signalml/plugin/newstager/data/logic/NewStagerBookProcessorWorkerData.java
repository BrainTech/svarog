package org.signalml.plugin.newstager.data.logic;

import java.util.Map;

import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerFixedParameters;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerSleepStats;
import org.signalml.plugin.newstager.logic.book.NewStagerBookDataProvider;

public class NewStagerBookProcessorWorkerData {

	public final NewStagerBookDataProvider provider;
	public final INewStagerWorkerCompletion<NewStagerBookProcessorResult> completion;
	public final NewStagerConstants constants;
	public final Map<String, Integer> channelMap;
	public final NewStagerParameters parameters;
	public final NewStagerFixedParameters fixedParameters;
	public final double muscle[];
	public final NewStagerSleepStats signalStatCoeffs;

	public NewStagerBookProcessorWorkerData(
		final NewStagerBookDataProvider provider,
		final INewStagerWorkerCompletion<NewStagerBookProcessorResult> completion,
		final NewStagerConstants constants,
		final Map<String, Integer> channelMap,
		final NewStagerParameters parameters,
		final NewStagerFixedParameters fixedParameters,
		final double muscle[], final NewStagerSleepStats signalStatCoeffs) {
		this.provider = provider;
		this.completion = completion;
		this.constants = constants;
		this.channelMap = channelMap;
		this.parameters = parameters;
		this.fixedParameters = fixedParameters;
		this.muscle = muscle;
		this.signalStatCoeffs = signalStatCoeffs;
	}

}
