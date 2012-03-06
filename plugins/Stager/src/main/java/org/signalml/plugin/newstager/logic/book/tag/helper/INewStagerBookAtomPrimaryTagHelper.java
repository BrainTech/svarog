package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.Collection;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerHelperTagSample;

public interface INewStagerBookAtomPrimaryTagHelper {

	Collection<NewStagerHelperTagSample> convertToTagSamples(NewStagerBookSample sample);
	
}
