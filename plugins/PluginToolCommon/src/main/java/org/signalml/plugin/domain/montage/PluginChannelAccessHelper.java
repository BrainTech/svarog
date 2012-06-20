package org.signalml.plugin.domain.montage;

import java.util.Map;

import org.signalml.plugin.exception.PluginAlgorithmDataException;

public class PluginChannelAccessHelper {
	public static double[] GetChannelSignal(Map<String, Integer> channels,
											PluginChannel channel, double signal[][])
	throws PluginAlgorithmDataException {
		return signal[GetChannelNumber(channels, channel, signal)];
	}

	public static Integer GetChannelNumber(Map<String, Integer> channels,
										   PluginChannel channel, double[][] signal)
	throws PluginAlgorithmDataException {
		Integer channelNum = channels.get(channel.toString());
		if (channelNum == null || channelNum < 0
				|| (signal != null && channelNum >= signal.length)) {
			throw new PluginAlgorithmDataException("Invalid channel "
												   + channel.toString());
		}
		return channelNum;
	}
}
