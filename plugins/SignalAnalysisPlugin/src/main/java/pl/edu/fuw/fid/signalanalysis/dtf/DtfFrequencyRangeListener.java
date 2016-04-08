package pl.edu.fuw.fid.signalanalysis.dtf;

/**
 * Becomes notified whenever user changes frequency range.
 *
 * @author ptr@mimuw.edu.pl
 */
public interface DtfFrequencyRangeListener {

	public void frequencyRangeChanged(Double freqMin, Double freqMax);

}
