package org.signalml.plugin.newstager.data.logic;

import java.util.Collection;

import org.signalml.plugin.newstager.data.NewStagerBookInfo;

public class NewStagerBookProcessorStepResult extends NewStagerComputationMgrStepResult {

	public final NewStagerBookInfo bookInfo;
	public final Collection<NewStagerBookProcessorResult> tagResults;
	public final boolean montage[];

	public NewStagerBookProcessorStepResult(NewStagerBookInfo bookInfo,
											Collection<NewStagerBookProcessorResult> tagResults,
											boolean montage[]) {
		this.bookInfo = bookInfo;
		this.tagResults = tagResults;
		this.montage = montage;
	}

}
