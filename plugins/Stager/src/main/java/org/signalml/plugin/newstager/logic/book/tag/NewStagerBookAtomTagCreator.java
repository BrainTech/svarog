package org.signalml.plugin.newstager.logic.book.tag;

import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginChannelTagDef;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomTagCreatorData;

public class NewStagerBookAtomTagCreator {

	private final double epochSize;
	private final double frequency;

	public NewStagerBookAtomTagCreator(NewStagerBookAtomTagCreatorData data) {
		this.epochSize = data.constants.blockLengthInSeconds;
		this.frequency = data.bookInfo.samplingFrequency;
	}

	public IPluginTagDef createPageTag(final double offset) {
		return new IPluginTagDef() {

			@Override
			public double getOffset() {
				return offset * epochSize;
			}

			@Override
			public double getLength() {
				return epochSize;
			}

			@Override
			public int getChannel() {
				return -1;
			}

		};
	}

	public IPluginTagDef createChannelTag(double offset, double length, int channel) {
		return new PluginChannelTagDef(offset / this.frequency, length
				/ this.frequency, channel);
	}

}
