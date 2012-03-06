package org.signalml.plugin.newstager.logic.book.tag;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerTagBuilderResult;

public interface INewStagerTagBuilder {

	boolean process(NewStagerBookSample sample);

	NewStagerTagBuilderResult getResult();

}
