package org.signalml.plugin.newstager.data.logic;


public interface INewStagerWorkerCompletion<ResultType> {

	void completeWork(ResultType result);

}
