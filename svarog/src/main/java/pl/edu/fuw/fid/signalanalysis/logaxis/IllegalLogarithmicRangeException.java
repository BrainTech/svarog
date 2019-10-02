package pl.edu.fuw.fid.signalanalysis.logaxis;

/**
 * Exception to be thrown when a bound value isn't supported by the logarithmic axis.
 *
 * @author Kevin Senechal <kevin.senechal@dooapp.com>
 */
public class IllegalLogarithmicRangeException extends Exception {

	/**
	 * @param string
	 */
	public IllegalLogarithmicRangeException(String message) {
		super(message);
	}

}