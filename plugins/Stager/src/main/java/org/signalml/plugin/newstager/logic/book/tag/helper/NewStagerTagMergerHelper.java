package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginTagDefRangeComparator;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorResult;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorStepResult;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagCreatorData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollection;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerBookAtomTagCreator;

public class NewStagerTagMergerHelper {

	public static Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> MergeTags(
			NewStagerBookProcessorStepResult stepTagResult,
			NewStagerConstants constants) {
		Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> map = new HashMap<NewStagerTagCollectionType, Collection<IPluginTagDef>>();
		Comparator<IPluginTagDef> comparator = new PluginTagDefRangeComparator();

		Set<NewStagerTagCollectionType> sleepStages = NewStagerTagCollectionType
				.GetSleepStages();
		SortedSet<IPluginTagDef> allSleepPageTags = new TreeSet<IPluginTagDef>(
				comparator);
		for (NewStagerBookProcessorResult tagCollection : stepTagResult.tagResults) {
			for (Entry<NewStagerTagCollectionType, NewStagerTagCollection> entry : tagCollection.tagCollectionMap
					.entrySet()) {
				NewStagerTagCollectionType key = entry.getKey();

				Collection<IPluginTagDef> tags = map.get(key);
				if (tags == null) {
					tags = new TreeSet<IPluginTagDef>(comparator);
					map.put(key, tags);
				} else {

				}

				tags.addAll(entry.getValue().tags);

				if (sleepStages.contains(key)) {
					allSleepPageTags.addAll(tags);
				}
			}
		}

		NewStagerTagMergerHelper
				.ConsolidateTags(constants, map, allSleepPageTags,
						stepTagResult.bookInfo, stepTagResult.montage);

		return map;
	}

	private static void ConsolidateTags(NewStagerConstants constants,
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> map,
			SortedSet<IPluginTagDef> allSleepPageTags,
			NewStagerBookInfo bookInfo, boolean montage[]) {

		if (allSleepPageTags.size() == 0) {
			return;
		}

		Map<IPluginTagDef, NewStagerTagCollectionType> reverseMap = createReverseMap(map);

		for (NewStagerTagCollectionType tagType : NewStagerTagCollectionType
				.GetConsolidatedSleepStages()) {
			map.put(tagType, new LinkedList<IPluginTagDef>());
		}

		NewStagerBookAtomTagCreator tagCreator = new NewStagerBookAtomTagCreator(
				new NewStagerBookAtomTagCreatorData(constants, bookInfo));

		Iterator<IPluginTagDef> it = allSleepPageTags.iterator();
		if (!it.hasNext()) {
			return;
		}

		IPluginTagDef prevTag = it.next();
		map.get(NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_M).add(
				tagCreator.createPageTag(prevTag.getOffset()));

		if (!it.hasNext()) {
			return;
		}

		IPluginTagDef nextTag = it.next();
		NewStagerTagCollectionType nextKey = null;
		NewStagerTagCollectionType prevKey = reverseMap.get(prevTag);

		int i = 0;
		while (it.hasNext()) {
			++i;
			NewStagerTagCollectionType tagTypeToCreate;

			IPluginTagDef tag = nextTag;
			nextTag = it.next();

			NewStagerTagCollectionType key = reverseMap.get(tag);
			nextKey = reverseMap.get(nextTag);

			tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_M;

			if (key != null) {
				if (prevKey != key && nextKey != key) {
					key = prevKey;
				}

				tagTypeToCreate = NewStagerTagMergerHelper
						.GetConsolidatedTagType(key, i, montage);
			}

			NewStagerTagMergerHelper.AddTag(tagCreator, map, tag,
					tagTypeToCreate);

			prevKey = key;
		}

		NewStagerTagMergerHelper.AddTag(tagCreator, map, nextTag,
				NewStagerTagMergerHelper.GetConsolidatedTagType(nextKey, i + 1,
						null));

	}

	private static void AddTag(NewStagerBookAtomTagCreator tagCreator,
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> map,
			IPluginTagDef tag, NewStagerTagCollectionType tagTypeToCreate) {
		map.get(tagTypeToCreate).add(
				tagCreator.createPageTag(tag.getOffset() / tag.getLength()));

	}

	private static Map<IPluginTagDef, NewStagerTagCollectionType> createReverseMap(
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> map) {
		Map<IPluginTagDef, NewStagerTagCollectionType> reverseMap = new IdentityHashMap<IPluginTagDef, NewStagerTagCollectionType>();

		Set<NewStagerTagCollectionType> sleepStages = NewStagerTagCollectionType
				.GetSleepStages();

		for (Entry<NewStagerTagCollectionType, Collection<IPluginTagDef>> entry : map
				.entrySet()) {
			NewStagerTagCollectionType key = entry.getKey();
			if (!sleepStages.contains(key)) {
				continue;
			}

			for (IPluginTagDef tag : entry.getValue()) {
				reverseMap.put(tag, key);
			}
		}
		return reverseMap;
	}

	private static NewStagerTagCollectionType GetConsolidatedTagType(
			NewStagerTagCollectionType key, int tagPosition, boolean montage[]) {
		if (montage == null || tagPosition >= montage.length
				|| !montage[tagPosition]) {
			switch (key) {
			case SLEEP_STAGE_1:
				return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_1;
			case SLEEP_STAGE_2:
				return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_2;
			case SLEEP_STAGE_3:
				return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_3;
			case SLEEP_STAGE_4:
				return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_4;
			case SLEEP_STAGE_R:
				return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_REM;
			case SLEEP_STAGE_W:
				return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_W;
			default:
				break;
			}
		}

		return NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_M;
	}

}
