package org.signalml.codec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.DoubleBuffer;
import java.nio.BufferUnderflowException;
import static java.lang.String.format;
import static org.signalml.app.util.i18n.SvarogI18n._;

import org.apache.log4j.Logger;

public class JsignalmlReader implements SignalMLCodecReader {
	protected static final Logger log = Logger.getLogger(SignalMLCodecReader.class);

	final jsignalml.Source source;
	final SignalMLCodec codec;

	JsignalmlReader(jsignalml.Source source, SignalMLCodec codec) {
		this.source = source;
		this.codec = codec;
	}

	@Override
	public SignalMLCodec getCodec() {
		return this.codec;
	}

	@Override
	public void open(String filename) throws SignalMLCodecException {
		try {
			this.source.open(new File(filename));
		} catch(FileNotFoundException e) {
			throw new SignalMLCodecException(_("cannot open file"), e);
		} catch(IOException e) {
			throw new SignalMLCodecException(_("cannot read file"), e);
		}
	}

	@Override
	public void close() {
		this.source.close();
	}

	@Override
	public boolean is_number_of_channels() throws SignalMLCodecException {
		return true;
	}

	@Override
	public int get_number_of_channels() throws SignalMLCodecException {
		return this.source.get_set().getNumberOfChannels();
	}

	@Override
	public String getFormatID() throws SignalMLCodecException {
		return this.source.getFormatID();
	}

	@Override
	public String getFormatDescription() throws SignalMLCodecException {
		return this.source.getFormatInfo();
	}

	@Override
	public float get_sampling_frequency(int channel) throws SignalMLCodecException {
		return (float) this.source.get_set().getChannel(channel).getSamplingFrequency();
	}

	@Override
	public float get_sampling_frequency() throws SignalMLCodecException {
		try {
			return (float) this.source.get_set().getSamplingFrequency();
			// will fail if not uniform
		} catch(RuntimeException e) {
			throw new SignalMLCodecException(e);
		}
	}

	@Override
	public boolean is_sampling_frequency() throws SignalMLCodecException {
		return true;
	}

	@Override
	public boolean is_uniform_sampling_frequency() throws SignalMLCodecException {
		return this.source.get_set().hasUniformSamplingFrequency();
	}

	@Override
	public int get_max_offset() throws SignalMLCodecException {
		long value = this.source.get_set().getMaxNumberOfSamples();
		if (value > Integer.MAX_VALUE)
			throw new SignalMLCodecException("32-bits suck");
		if (value <= 0)
			throw new SignalMLCodecException("empty channel");
		return (int)value - 1;
	}

	@Override
	public boolean is_channel_names() throws SignalMLCodecException {
		return true;
	}

	@Override
	public String[] get_channel_names() throws SignalMLCodecException {
		final jsignalml.ChannelSet set = this.source.get_set();
		int n = set.getNumberOfChannels();
		String names[] = new String[n];
		for (int i=0; i<n; i++)
			names[i] = set.getChannel(i).getChannelName();
		return names;
	}

	@Override
	public boolean is_calibration() throws SignalMLCodecException {
		return true;
	}

	@Override
	public float get_calibration(int channel) throws SignalMLCodecException {
		return 1.0f;
	}

	@Override
	public float get_calibration() throws SignalMLCodecException {
		return 1.0f;
	}

	@Override
	public void set_sampling_frequency(float freq) throws SignalMLCodecException {
		throw new SignalMLCodecException("go away");
	}

	@Override
	public void set_number_of_channels(int n) throws SignalMLCodecException {
		throw new SignalMLCodecException("go away");
	}

	@Override
	public void set_calibration(float calib) throws SignalMLCodecException {
		throw new SignalMLCodecException("go away");
	}

	@Override
	public String getCurrentFilename() {
		return this.source.getCurrentFilename().toString();
	}

	@Override
	public float getChannelSample(long offset, int chn) throws SignalMLCodecException {
		try {
			return this.source.get_set().getChannel(chn).getSample(offset);
		} catch(RuntimeException e) {
			log.info(format("%d / %d: %s", offset, chn, e));
			throw e;
		}
	}

	@Override
	public void getSamples(FloatBuffer dst, int chn, long sample)
		throws BufferUnderflowException
	{
		this.source.get_set().getChannel(chn).getSamples(dst, sample);
	}

	@Override
	public void getSamples(DoubleBuffer dst, int chn, long sample)
		throws BufferUnderflowException
	{
		FloatBuffer buf = FloatBuffer.allocate(dst.remaining());
		this.source.get_set().getChannel(chn).getSamples(buf, sample);
		while(buf.hasRemaining())
			dst.put(buf.get());
	}
}
