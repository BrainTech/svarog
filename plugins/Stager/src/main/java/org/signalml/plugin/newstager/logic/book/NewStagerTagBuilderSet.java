package org.signalml.plugin.newstager.logic.book;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerTagBuilderResult;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollection;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.plugin.newstager.logic.book.tag.INewStagerTagBuilder;

public class NewStagerTagBuilderSet {

	private final List<INewStagerTagBuilder> builders;

	public NewStagerTagBuilderSet() {
		this.builders = new LinkedList<INewStagerTagBuilder>();
	}

	public NewStagerTagBuilderSet(Collection<INewStagerTagBuilder> builders) {
		this.builders = new LinkedList<INewStagerTagBuilder>(builders);
	}

	public void add(INewStagerTagBuilder tagBuilder) {
		this.builders.add(tagBuilder);
	}

	public void process(NewStagerBookSample sample) {
		for (INewStagerTagBuilder builder : this.builders) {
			builder.process(sample);
		}
	}

	public Map<NewStagerTagCollectionType, NewStagerTagCollection> getResult() {
		Map<NewStagerTagCollectionType, NewStagerTagCollection> result = new HashMap<NewStagerTagCollectionType, NewStagerTagCollection>();
		SortedSet<IPluginTagDef> all = new TreeSet<IPluginTagDef>(new Comparator<IPluginTagDef>() {

			@Override
			public int compare(IPluginTagDef o1, IPluginTagDef o2) {
				double x = o1.getOffset();
				double y = o2.getOffset();
				return x < y ? -1 : (x == y ? 0 : 1);
			}
		});
		for (INewStagerTagBuilder builder : this.builders) {
			NewStagerTagBuilderResult builderResult = builder.getResult();
			if (builderResult == null || !builderResult.freshResult) {
				continue;
			}

			for (NewStagerTagCollection collection : builderResult.tagMap.values()) {
				all.addAll(collection.tags);

				NewStagerTagCollection existingCollection = result.get(collection.type);
				if (existingCollection == null) {
					result.put(collection.type, collection);
				} else {
					List<IPluginTagDef> tags = new LinkedList<IPluginTagDef>(existingCollection.tags);
					tags.addAll(collection.tags);
					existingCollection = new NewStagerTagCollection(collection.type, tags);
					result.put(existingCollection.type, existingCollection);
				}
			}
		}

		return result;
	}
}
