package org.signalml.plugin.newstager.logic.book.tag;

import java.util.LinkedList;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;

public class NewStagerBookAtomSimpleTagBuilder extends NewStagerAbstractTagBuilder {

	private NewStagerTagCollectionType tagType;

	public NewStagerBookAtomSimpleTagBuilder(
		NewStagerBookAtomTagBuilderData data,
		NewStagerTagCollectionType tagType) {
		super(data);
		this.tagType = tagType;
		this.tags = new LinkedList<IPluginTagDef>();
	}

	@Override
	public boolean process(NewStagerBookSample sample) {
		this.tags.add(this.data.tagCreator.createPageTag(sample.offset));
		return true;
	}

	@Override
	protected NewStagerTagCollectionType getTagType() {
		return this.tagType;
	}

}
