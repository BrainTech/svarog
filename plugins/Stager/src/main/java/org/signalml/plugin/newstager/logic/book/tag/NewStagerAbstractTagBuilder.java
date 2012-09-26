package org.signalml.plugin.newstager.logic.book.tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagBuilderResult;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollection;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;

public abstract class NewStagerAbstractTagBuilder implements
	INewStagerTagBuilder {

	protected Collection<IPluginTagDef> tags;
	protected NewStagerTagCollection computedResultCollection;
	protected final NewStagerBookAtomTagBuilderData data;
	
	protected String description;

	public NewStagerAbstractTagBuilder(NewStagerBookAtomTagBuilderData data) {
		this.data = data;
		this.computedResultCollection = null;
	}
	
	public NewStagerAbstractTagBuilder(NewStagerBookAtomTagBuilderData data, String description) {
		this(data);
		this.description = description;
	}

	@Override
	public NewStagerTagBuilderResult getResult() {
		boolean freshFlag = true;
		if (this.computedResultCollection == null) {
			this.computedResultCollection = new NewStagerTagCollection(
				this.getTagType(), this.tags);
		} else {
			freshFlag = false;
		}

		Map<NewStagerTagCollectionType, NewStagerTagCollection> map = new HashMap<NewStagerTagCollectionType, NewStagerTagCollection>(
			1);
		map.put(this.getTagType(), this.computedResultCollection);
		return new NewStagerTagBuilderResult(map, freshFlag);
	}

	@Override
	public String toString() {
		return this.description != null ? this.description : super.toString();
	}
	
	protected abstract NewStagerTagCollectionType getTagType();	
}
