package org.signalml.plugin.newstager.data;

import java.util.Map;

import org.signalml.plugin.newstager.data.logic.INewStagerWorkerCompletion;
import org.signalml.plugin.newstager.data.logic.NewStagerStatAlgorithmResult;
import org.signalml.plugin.newstager.io.INewStagerStatsSynchronizer;

public class NewStagerStatWorkerData {

	public final INewStagerStatsSynchronizer synchronizer;
	public final INewStagerWorkerCompletion<NewStagerStatAlgorithmResult> completion;
	public final NewStagerConstants constants;
	public final NewStagerParameters parameters;
	public final Map<String, Integer> channelMap;

	public NewStagerStatWorkerData(
		INewStagerStatsSynchronizer synchronizer,
		INewStagerWorkerCompletion<NewStagerStatAlgorithmResult> completion,
		NewStagerConstants constants, NewStagerParameters parameters,
		Map<String, Integer> channelMap) {
		this.synchronizer = synchronizer;
		this.completion = completion;
		this.constants = constants;
		this.parameters = parameters;
		this.channelMap = channelMap;
	}

}
