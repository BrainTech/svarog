/* InputDataException.java created 2007-09-12
 *
 */
package org.signalml.method;

import org.springframework.validation.BindException;

/**
 *  An exception representing invalid method input data. Use Spring's standard
 *  Errors interface implemented by BindException to retrieve the list of encountered
 *  problems.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class InputDataException extends BindException {

	private static final long serialVersionUID = 1L;

        /**
         * Creates new exception with specified data which caused it and name of this object.
         * @param target data which caused this exception
         * @param objectName name of object which caused this exception
         */
	public InputDataException(Object target, String objectName) {
		super(target, objectName);
	}

}
