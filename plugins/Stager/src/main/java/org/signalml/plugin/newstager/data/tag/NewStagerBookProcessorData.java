package org.signalml.plugin.newstager.data.tag;

import java.util.Map;

import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerFixedParameters;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerSleepStats;

public class NewStagerBookProcessorData {

	public final NewStagerConstants constants;
	public final Map<String, Integer> channelMap;
	public final NewStagerParameters parameters;
	public final NewStagerFixedParameters fixedParameters;
	public final NewStagerBookInfo bookInfo;
	public final double muscle[];
	public final NewStagerSleepStats signalStatCoeffs;
	

	public NewStagerBookProcessorData(
			final NewStagerConstants constants,
			final Map<String, Integer> channelMap,
			final NewStagerParameters parameters,
			final NewStagerFixedParameters fixedParameters,
			final double muscle[],
			final NewStagerSleepStats signalStatCoeffs,
			final NewStagerBookInfo bookInfo) {
		this.constants = constants;
		this.channelMap = channelMap;
		this.parameters = parameters;
		this.fixedParameters = fixedParameters;
		this.muscle = muscle;
		this.signalStatCoeffs = signalStatCoeffs;
		this.bookInfo = bookInfo;
	}

}
