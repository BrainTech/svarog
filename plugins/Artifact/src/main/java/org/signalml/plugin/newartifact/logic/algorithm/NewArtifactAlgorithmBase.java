package org.signalml.plugin.newartifact.logic.algorithm;

import java.util.Arrays;

import org.signalml.plugin.domain.montage.PluginChannel;
import org.signalml.plugin.domain.montage.PluginChannelAccessHelper;
import org.signalml.plugin.exception.PluginAlgorithmDataException;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.exception.NewArtifactPluginException;


public abstract class NewArtifactAlgorithmBase implements INewArtifactAlgorithm {

	protected final static double DELTA = 0.000001d;

	protected double resultBuffer[][];
	protected final NewArtifactConstants constants;

	public NewArtifactAlgorithmBase(NewArtifactConstants constants) {
		this.resultBuffer = null;
		this.constants = constants;
	}

	protected double[][] zeros(int x, int y) {
		double result[][] = new double[x][y];
		for (int i = 0; i < x; ++i) {
			Arrays.fill(result[i], 0.0);
		}
		return result;
	}

	protected int getChannelNumber(NewArtifactAlgorithmData data, PluginChannel channel) throws NewArtifactPluginException {
		try {
			return PluginChannelAccessHelper.GetChannelNumber(data.channels, channel, data.signal);
		} catch (PluginAlgorithmDataException e) {
			throw new NewArtifactPluginException(e);
		}
	}

	protected double[] getChannelData(NewArtifactAlgorithmData data,
									  PluginChannel channelName) throws NewArtifactPluginException {
		try {
			return PluginChannelAccessHelper.GetChannelSignal(data.channels, channelName, data.signal);
		} catch (PluginAlgorithmDataException e) {
			throw new NewArtifactPluginException(e);
		}
	}


}
