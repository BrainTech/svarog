package org.signalml.codec;

public interface SignalMLCodecReader {
	public SignalMLCodec getCodec();

	void open(String filename) throws SignalMLCodecException;
	void close();

	public boolean is_number_of_channels() throws SignalMLCodecException;

	public int get_number_of_channels() throws SignalMLCodecException;

	public String getFormatID() throws SignalMLCodecException;

	public String getFormatDescription() throws SignalMLCodecException;

	public float get_sampling_frequency(int channel) throws SignalMLCodecException;

	public float get_sampling_frequency() throws SignalMLCodecException;

	public boolean is_sampling_frequency() throws SignalMLCodecException;

	public boolean is_uniform_sampling_frequency() throws SignalMLCodecException;

	public int get_max_offset() throws SignalMLCodecException;

	public boolean is_channel_names() throws SignalMLCodecException;

	public String[] get_channel_names() throws SignalMLCodecException;

	public boolean is_calibration() throws SignalMLCodecException;

	public float get_calibration(int channel) throws SignalMLCodecException;

	public float get_calibration() throws SignalMLCodecException;

	public void set_sampling_frequency(float freq) throws SignalMLCodecException;

	public void set_number_of_channels(int n) throws SignalMLCodecException;

	public void set_calibration(float calib) throws SignalMLCodecException;

	public String getCurrentFilename();

	public float getChannelSample(long offset, int chn) throws SignalMLCodecException;
}
