package org.signalml.plugin.newstager.logic.book.tag;

import java.util.Collection;
import java.util.LinkedList;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.domain.montage.PluginChannel;
import org.signalml.plugin.domain.montage.PluginChannelAccessHelper;
import org.signalml.plugin.exception.PluginAlgorithmDataException;
import org.signalml.plugin.newstager.data.book.NewStagerBookSample;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagBuilderData;
import org.signalml.plugin.newstager.data.tag.NewStagerHelperTagSample;
import org.signalml.plugin.newstager.logic.book.tag.helper.INewStagerBookAtomPrimaryTagHelper;

public abstract class NewStagerPrimaryTagBuilder extends NewStagerAbstractTagBuilder {

	private INewStagerBookAtomPrimaryTagHelper helper;
	private int channel;

	public NewStagerPrimaryTagBuilder(NewStagerBookAtomTagBuilderData data) {
		super(data);
		this.tags = new LinkedList<IPluginTagDef>();

		this.helper = this.getConverter();

		try {
			this.channel = PluginChannelAccessHelper.GetChannelNumber(data.channelMap, PluginChannel.C3, null);
		} catch (PluginAlgorithmDataException e) {
			this.channel = -1;
		}
	}

	@Override
	public boolean process(NewStagerBookSample sample) {
		if (this.channel == -1) {
			return false;
		}

		Collection<NewStagerHelperTagSample> tagSamples = this.helper.convertToTagSamples(sample);
		NewStagerBookAtomTagCreator tagCreator = data.tagCreator;
		for (NewStagerHelperTagSample tagSample : tagSamples) {
			this.tags.add(tagCreator.createChannelTag(tagSample.offset, tagSample.length, this.channel)); //TODO
		}
		return true; //TODO
	}

	protected abstract INewStagerBookAtomPrimaryTagHelper getConverter();
}
