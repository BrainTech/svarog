package org.signalml.math.iirdesigner;

import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This exception class indicates that the filter designer designed the
 * filter for the given parameters, but the filter is instable.
 *
 * @author Piotr Szachewicz
 */
public class FilterNotStableException extends Exception {

	/**
	 * Constructs a new exception with the specified detail message.
	 */
	public FilterNotStableException() {
		super(_("The filter designed is not stable."));
	}

}