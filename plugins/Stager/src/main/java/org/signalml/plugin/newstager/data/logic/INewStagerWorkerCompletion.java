package org.signalml.plugin.newstager.data.logic;


public interface INewStagerWorkerCompletion<ResultType> {

	void signalProgress(int i);

	void completeWork(ResultType result);

}
