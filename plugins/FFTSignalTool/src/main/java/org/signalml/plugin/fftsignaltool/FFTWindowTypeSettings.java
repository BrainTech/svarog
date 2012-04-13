/* FFTWindowTypeSettings.java created 2008-02-04
 *
 */
package org.signalml.plugin.fftsignaltool;

import org.signalml.plugin.fft.export.WindowType;

/**
 * Interface which allows to get and set the parameters of the window function,
 * namely its {@link WindowType type} and parameter.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public interface FFTWindowTypeSettings {

	/**
	 * Returns the {@link WindowType type} of the window function.
	 * @return the type of the window function
	 */
	WindowType getWindowType();

	/**
	 * Sets the {@link WindowType type} of the window function.
	 * @param windowType the type of the window function
	 */
	void setWindowType(WindowType windowType);

	/**
	 * Returns the parameter of the window function.
	 * @return the parameter of the window function
	 */
	double getWindowParameter();

	/**
	 * Sets the parameter of the window function.
	 * @param windowParameter the parameter of the window function
	 */
	void setWindowParameter(double windowParameter);

}