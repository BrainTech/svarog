package pl.edu.fuw.fid.signalanalysis;

/**
 * @author ptr@mimuw.edu.pl
 */
public interface MultiSignal {

	public int getChannelCount();

	public int getSampleCount();

	public void getSamples(int channel, int start, int length, double[] buffer);

	public double getSamplingFrequency();

}
