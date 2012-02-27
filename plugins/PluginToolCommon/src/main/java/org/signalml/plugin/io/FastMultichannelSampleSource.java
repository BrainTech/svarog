package org.signalml.plugin.io;

import java.beans.PropertyChangeListener;

import org.signalml.codec.SignalMLCodecException;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.domain.signal.MultichannelSampleSource;

public class FastMultichannelSampleSource implements MultichannelSampleSource {

	private static int BUFFER_SIZE = Math.max(1 << 20, Integer
					 .highestOneBit(Math.min((int) Runtime.getRuntime().maxMemory(),
							 Integer.MAX_VALUE) / 20));

	private SignalMLCodecReader delegate;
	private short buffer[];
	private int offset;

	private String names[];

	public FastMultichannelSampleSource(SignalMLCodecReader codec) {
		this.delegate = codec;
		this.buffer = null;
		this.offset = -BUFFER_SIZE - 1;
		this.names = null;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

	}

	@Override
	public void destroy() {
	}

	@Override
	public int getChannelCount() {
		try {
			return this.delegate.get_number_of_channels();
		} catch (SignalMLCodecException e) {
			return -1;
		}
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLabel(int channel) {
		if (this.names == null) {
			try {
				this.names = this.delegate.get_channel_names();
			} catch (SignalMLCodecException e) {
				// do nothing
			}
		}

		try {
			return this.names == null ? null : this.names[channel];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public int getSampleCount(int channel) {
		try {
			return this.delegate.get_max_offset() + 1;
		} catch (SignalMLCodecException ex) {
			return 0;
		}
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset,
			       int count, int arrayOffset) {
		if (count <= 0) {
			return;
		}

		int channelCount = this.getChannelCount();
		int chunkOffset;
		signalOffset = channelCount * signalOffset + channel;

		while (count > 0) {
			int end = this.offset + BUFFER_SIZE;
			chunkOffset = signalOffset - this.offset;
			while (signalOffset >= this.offset && end > signalOffset) {
				target[arrayOffset] = (float) this.buffer[chunkOffset];
				arrayOffset++;
				count--;
				signalOffset += channelCount;
				chunkOffset += channelCount;
				if (count <= 0) {
					return;
				}
			}

			this.offset = signalOffset;
			try {
				this.buffer = this.delegate.getSamples(signalOffset,
								       BUFFER_SIZE);
			} catch (SignalMLCodecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public float getSamplingFrequency() {
		try {
			return this.delegate.get_sampling_frequency();
		} catch (SignalMLCodecException e) {
			return 128.0f;
		}
	}

	@Override
	public boolean isChannelCountCapable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

}
