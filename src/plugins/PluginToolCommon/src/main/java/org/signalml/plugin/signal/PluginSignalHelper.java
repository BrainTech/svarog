package org.signalml.plugin.signal;

import org.signalml.domain.signal.MultichannelSampleSource;

public class PluginSignalHelper {
	public static int GetBlockCount(MultichannelSampleSource source, int blockLength) {
		int channelCount = source.getChannelCount();
		int count = 0;
		for (int i = 0; i < channelCount; ++i) {
			count = Math.max(source.getSampleCount(i) / blockLength, count);
		}

		return count;
	}
}
