package pl.edu.fuw.fid.signalanalysis.dtf;

/**
 * Becomes notified whenever user chooses order for AR model.
 *
 * @author ptr@mimuw.edu.pl
 */
interface DtfOrderSelectionListener {

	public void modelOrderSelected(int order);

}
