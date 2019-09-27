package org.signalml.plugin.domain.montage;

import java.util.Map;

import org.signalml.plugin.exception.PluginAlgorithmDataException;
import static org.signalml.app.util.i18n.SvarogI18n._;

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
			throw new PluginAlgorithmDataException(String.format(_("Invalid channel %s"), channel.toString()));
		}
		return channelNum;
	}
}
