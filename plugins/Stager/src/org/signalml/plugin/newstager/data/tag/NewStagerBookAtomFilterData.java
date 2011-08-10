package org.signalml.plugin.newstager.data.tag;

import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;
import org.signalml.plugin.newstager.logic.book.tag.helper.NewStagerBookAtomSampleHelperSet;

public class NewStagerBookAtomFilterData {

	public NewStagerBookInfo bookInfo;
	public NewStagerFASPThreshold threshold;
	public NewStagerBookAtomSampleHelperSet converterSet;

	public NewStagerBookAtomFilterData(NewStagerBookInfo bookInfo,
					   NewStagerFASPThreshold threshold,
					   NewStagerBookAtomSampleHelperSet converterSet) {
		this.bookInfo = bookInfo;
		this.threshold = threshold;
		this.converterSet = converterSet;
	}
}
