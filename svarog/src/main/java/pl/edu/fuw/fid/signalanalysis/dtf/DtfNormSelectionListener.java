package pl.edu.fuw.fid.signalanalysis.dtf;

/**
 * Becomes notified whenever user selects "normalized" or "not normalized"
 * variant of the DTF method.
 *
 * @author ptr@mimuw.edu.pl
 */
public interface DtfNormSelectionListener {

	public void normalizedChanged(boolean normalized);

}
