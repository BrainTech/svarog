package pl.edu.fuw.fid.signalanalysis;

/**
 * @author ptr@mimuw.edu.pl
 */
public interface MultiSignal {

	public int getChannelCount();

	public double[] getData(int channel);

	public int getSampleCount();

	public double getSamplingFrequency();

}
