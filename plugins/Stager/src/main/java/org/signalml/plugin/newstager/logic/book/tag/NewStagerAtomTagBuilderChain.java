package org.signalml.plugin.newstager.logic.book.tag;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagBuilderResult;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollection;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.plugin.newstager.logic.book.NewStagerTagBuilderSet;

public class NewStagerAtomTagBuilderChain extends NewStagerAbstractTagBuilder {

	List<INewStagerTagBuilder> builders = new LinkedList<INewStagerTagBuilder>();
	private NewStagerTagBuilderResult result;

	public NewStagerAtomTagBuilderChain(NewStagerBookAtomTagBuilderData data) {
		super(data);
	}

	public NewStagerAtomTagBuilderChain compose(INewStagerTagBuilder builder) {
		builders.add(builder);
		return this;
	}

	public NewStagerAtomTagBuilderChain composeChain() {
		NewStagerAtomTagBuilderChain chain = new NewStagerAtomTagBuilderChain(this.data);
		this.compose(chain);
		return chain;
	}


	@Override
	public boolean process(NewStagerBookSample sample) {
		for (INewStagerTagBuilder builder : this.builders) {
			if (builder.process(sample)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public NewStagerTagBuilderResult getResult() {
		if (this.result != null) {
			return new NewStagerTagBuilderResult(this.result.tagMap, false);
		}

		NewStagerTagBuilderSet builderSet = new NewStagerTagBuilderSet(this.builders);
		Map<NewStagerTagCollectionType, NewStagerTagCollection> builderSetResult = builderSet.getResult();


		this.result = new NewStagerTagBuilderResult(builderSetResult, true);

		return this.result;
	}

	@Override
	protected NewStagerTagCollectionType getTagType() {
		// TODO Auto-generated method stub
		return null;
	}
}
