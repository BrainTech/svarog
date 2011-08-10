package org.signalml.plugin.newstager.logic.book.tag.helper;

import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagHelperData;

public abstract class NewStagerBookAtomAbstractTagHelper {

	protected NewStagerBookAtomTagHelperData data;

	public NewStagerBookAtomAbstractTagHelper(NewStagerBookAtomTagHelperData data) {
		this.data = data;
	}

}
