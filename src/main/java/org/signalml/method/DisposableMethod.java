/**
 *
 */
package org.signalml.method;

import org.signalml.exception.SignalMLException;

/**
 *  This interface is to be implemented by those {@link Method methods} which need to dispose
 *  resources after use.
 *
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 *
 */
public interface DisposableMethod {

        /**
         * Disposes some resources after use.
         */
	void dispose() throws SignalMLException;

}
