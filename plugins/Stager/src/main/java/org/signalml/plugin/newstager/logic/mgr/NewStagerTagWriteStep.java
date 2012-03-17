package org.signalml.plugin.newstager.logic.mgr;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.signalml.method.ComputationException;
import org.signalml.plugin.data.logic.PluginComputationMgrStepResult;
import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginTagDefRangeComparator;
import org.signalml.plugin.exception.PluginToolAbortException;
import org.signalml.plugin.exception.PluginToolInterruptedException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.method.logic.AbstractPluginComputationMgrStep;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorResult;
import org.signalml.plugin.newstager.data.logic.NewStagerBookProcessorStepResult;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrStepData;
import org.signalml.plugin.newstager.data.logic.NewStagerTagWriteStepResult;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagCreatorData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollection;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.plugin.newstager.logic.book.tag.NewStagerBookAtomTagCreator;

public class NewStagerTagWriteStep extends
		AbstractPluginComputationMgrStep<NewStagerMgrStepData> {

	private File primaryTagFile;

	public NewStagerTagWriteStep(NewStagerMgrStepData data) {
		super(data);
		this.primaryTagFile = null;
	}

	@Override
	public int getStepNumberEstimate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected NewStagerTagWriteStepResult prepareStepResult() {
		if (this.primaryTagFile == null) {
			return null;
		}

		return new NewStagerTagWriteStepResult(
				this.primaryTagFile.getAbsolutePath());
	}

	@Override
	protected PluginComputationMgrStepResult doRun(
			PluginComputationMgrStepResult prevStepResult)
			throws PluginToolAbortException, PluginToolInterruptedException,
			ComputationException {

		NewStagerBookProcessorStepResult tagResult;
		try {
			tagResult = (NewStagerBookProcessorStepResult) prevStepResult;
		} catch (ClassCastException e) {
			throw new ComputationException(e);
		}

		Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tags = this
				.mergeTags(tagResult);

		try {
			this.writeTagsToFile(tags);
		} catch (IOException e) {
			throw new ComputationException(e);
		} catch (SignalMLException e) {
			throw new ComputationException(e);
		}

		return this.prepareStepResult();
	}

	private void writeTagsToFile(
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tagMap)
			throws IOException, SignalMLException {

		NewStagerTagWriter writer = new NewStagerTagWriter(this.data.stagerData);

		if (this.data.stagerData.getParameters().primaryHypnogramFlag) {
			writer.writeTags(NewStagerTagCollectionType.HYPNO_ALPHA,
					EnumSet.of(NewStagerTagCollectionType.HYPNO_ALPHA), tagMap);
			writer.writeTags(NewStagerTagCollectionType.HYPNO_DELTA,
					EnumSet.of(NewStagerTagCollectionType.HYPNO_DELTA), tagMap);
			writer.writeTags(NewStagerTagCollectionType.HYPNO_SPINDLE,
					EnumSet.of(NewStagerTagCollectionType.HYPNO_SPINDLE),
					tagMap);
		}

		writer.writeTags(NewStagerTagCollectionType.SLEEP_PAGES,
				this.getSleepStages(), tagMap);

		this.primaryTagFile = writer.writeTags(
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_PAGES,
				this.getConsolidatedSleepStages(), tagMap);
	}

	private EnumSet<NewStagerTagCollectionType> getSleepStages() {
		return EnumSet.of(NewStagerTagCollectionType.SLEEP_STAGE_1,
				NewStagerTagCollectionType.SLEEP_STAGE_2,
				NewStagerTagCollectionType.SLEEP_STAGE_3,
				NewStagerTagCollectionType.SLEEP_STAGE_4,
				NewStagerTagCollectionType.SLEEP_STAGE_R,
				NewStagerTagCollectionType.SLEEP_STAGE_W);
	}

	private EnumSet<NewStagerTagCollectionType> getConsolidatedSleepStages() {
		return EnumSet.of(
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_1,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_2,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_3,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_4,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_R,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_W,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_M);
	}

	private Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> mergeTags(
			NewStagerBookProcessorStepResult stepTagResult) {
		Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> map = new HashMap<NewStagerTagCollectionType, Collection<IPluginTagDef>>();
		Comparator<IPluginTagDef> comparator = new PluginTagDefRangeComparator();

		Set<NewStagerTagCollectionType> sleepStages = this.getSleepStages();
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

		this.consolidateTags(map, allSleepPageTags, stepTagResult.bookInfo,
				stepTagResult.montage);

		return map;
	}

	private void consolidateTags(
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> map,
			SortedSet<IPluginTagDef> allSleepPageTags,
			NewStagerBookInfo bookInfo, boolean montage[]) {

		if (allSleepPageTags.size() == 0) {
			return;
		}

		Set<NewStagerTagCollectionType> sleepStages = this.getSleepStages();
		Map<IPluginTagDef, NewStagerTagCollectionType> reverseMap = new IdentityHashMap<IPluginTagDef, NewStagerTagCollectionType>();
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

		for (NewStagerTagCollectionType tagType : this
				.getConsolidatedSleepStages()) {
			map.put(tagType, new LinkedList<IPluginTagDef>());
		}

		NewStagerBookAtomTagCreator tagCreator = new NewStagerBookAtomTagCreator(
				new NewStagerBookAtomTagCreatorData(this.data.constants,
						bookInfo));

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
		NewStagerTagCollectionType prevKey = reverseMap.get(prevTag);

		int i = 0;
		while (it.hasNext()) {
			++i;
			NewStagerTagCollectionType tagTypeToCreate;

			IPluginTagDef tag = nextTag;
			nextTag = it.next();

			NewStagerTagCollectionType key = reverseMap.get(tag);
			NewStagerTagCollectionType nextKey = reverseMap.get(nextTag);
			if (key == null) {
				// TODO error
			} else {
				if (prevKey != key && nextKey != key) {
					key = prevKey;
				}

				tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_M;
				if (montage == null || i >= montage.length || !montage[i]) {
					switch (key) {
					case SLEEP_STAGE_1:
						tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_1;
						break;
					case SLEEP_STAGE_2:
						tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_2;
						break;
					case SLEEP_STAGE_3:
						tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_3;
						break;
					case SLEEP_STAGE_4:
						tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_4;
						break;
					case SLEEP_STAGE_R:
						tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_R;
						break;
					case SLEEP_STAGE_W:
						tagTypeToCreate = NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_W;
						break;
					default:
						break;
					}
				}

				map.get(tagTypeToCreate).add(
						tagCreator.createPageTag(tag.getOffset()
								/ tag.getLength()));
			}

			prevKey = key;
		}

	}
}
