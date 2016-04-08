package pl.edu.fuw.fid.signalanalysis.ica;

/**
 * Exception signaling error (usually mathematical) in ICA method.
 *
 * @author ptr@mimuw.edu.pl
 */
public class IcaMethodException extends Exception {

	public IcaMethodException(String message) {
		super(message);
	}

}
