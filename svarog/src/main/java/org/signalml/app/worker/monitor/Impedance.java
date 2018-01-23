package org.signalml.app.worker.monitor;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Impedance {
	public final static int UNKNOWN = 0;
	public final static int NOT_APPLICABLE = 1;
	public final static int PRESENT = 2;

	private int[] flags;
	private float[][] data;
	private List<Integer> channels;

	public Impedance(DataInputStream data, int channelCount, int sampleCount) throws IOException
	{
		this.flags = readImpedanceFlags(data, channelCount);
		this.channels = channelsWithImpedance();
		this.data = this.channels.isEmpty() ? null : readImpedanceData(data, this.channels.size(), sampleCount);
	}

	private int[] readImpedanceFlags(DataInputStream data, int channelCount)
	{
		try {
			int[] flags = new int[channelCount];
			for (int channel = 0; channel < channelCount; ++channel) {
				flags[channel] = data.readByte();
			}
			return flags;
		} catch (IOException e) {
			return new int[0];
		}
	}

	private List<Integer> channelsWithImpedance()
	{
		List<Integer> channels = new ArrayList<>();
		for (int channel=0; channel<this.flags.length; ++channel) {
			if (this.flags[channel] == PRESENT) {
				channels.add(channel);
			}
		}
		return channels;
	}

	private float[][] readImpedanceData(DataInputStream data, int channelsCount, int sampleCount) throws IOException
	{
		float[][] impedance = new float[channelsCount][sampleCount];
		for (int sample = 0; sample < sampleCount; ++sample) {
			for (int channel = 0; channel < channelsCount; ++channel) {
				impedance[channel][sample] = data.readFloat();
			}
		}
		return impedance;
	}

	final public class ImpedanceData {
		final public int[] flags;
		final public Map<Integer, Float> data;

		public ImpedanceData(int[] flags, Map<Integer, Float> data)
		{
			this.flags = flags;
			this.data = data;
		}
	}

	public ImpedanceData sample(int timestampId)
	{
		Map<Integer, Float> data = new HashMap<>();
		for (int i=0; i < channels.size(); i++)
		{
			Integer channel = channels.get(i);
			data.put(channel, this.data[i][timestampId]);
		}
		return new ImpedanceData(this.flags, data);
	}
}
