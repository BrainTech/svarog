package org.signalml.plugin.newstager.data.tag;

import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerSleepStats;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerBookAtomSampleHelperSet;

public class NewStagerBookAtomTagHelperData {

	public final NewStagerBookInfo bookInfo;
	public final NewStagerBookAtomSampleHelperSet helperSet;
	public final double muscle[];
	public final NewStagerSleepStats signalStatCoeffs;

	public NewStagerBookAtomTagHelperData(NewStagerBookInfo bookInfo,
			NewStagerBookAtomSampleHelperSet helperSet, double muscle[],
			NewStagerSleepStats signalStatCoeffs) {
		this.bookInfo = bookInfo;
		this.helperSet = helperSet;
		this.muscle = muscle;
		this.signalStatCoeffs = signalStatCoeffs;
	}
}
