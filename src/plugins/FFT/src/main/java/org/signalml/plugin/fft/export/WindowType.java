/* WindowType.java created 2007-12-16
 *
 */

package org.signalml.plugin.fft.export;

import java.io.Serializable;



/**
 * Enumeration of possible types of window functions.
 * <p>
 * If the function contains the parameter ({@link #GAUSSIAN}, {@link #KAISER})
 * this class contains also the default, minimal and maximal value of this
 * parameter.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.,
 * Marcin Szumski
 */
public enum WindowType implements Serializable {

	/**
	 * the rectangular window function = no windowing
	 */
	RECTANGULAR(1),
	/**
	 * the Bartlett window function:<br>
	 * w(n) = 1 - |(2i - n)/(N-1))|
	 */
	BARTLETT(2),
	/**
	 * the Welch window function:<br>
	 * w(n) = 1 - ((2i - n)/(N-1)))^2
	 */
	WELCH(3),
	/**
	 * the Hann window function:<br>
	 * w(n) = 0.5(1- cos(2 pi n /(N-1)))
	 */
	HANN(4),
	/**
	 * the Hamming window function:<br>
	 * w(n) = 0.54 + 0.46 cos (2 pi n /(N-1))
	 */
	HAMMING(5),
	/**
	 * the Kaiser window function<br>
	 * @see <a href="http://en.wikipedia.org/wiki/Kaiser_window">Wikipedia</a>
	 */
	KAISER(6, true, 2.0, Double.MIN_VALUE, Double.MAX_VALUE),
	/**
	 * the gaussian window function:<br>
	 * w(n) = exp (-0.5 (alpha (2 n - N +1)/(N-1))^2 )
	 */
	GAUSSIAN(7, true, 2.5, 2.0, Double.MAX_VALUE)

	;

	/**
	 * the code of the window
	 */
	private int code;

	/**
	 * boolean which tells if the window function has a parameter
	 */
	private boolean parametrized;
	/**
	 * the default value of the parameter of the window function
	 */
	private double parameterDefault;
	/**
	 * the minimal value of the parameter of the window function
	 */
	private double parameterMin;
	/**
	 * the maximal value of the parameter of the window function
	 */
	private double parameterMax;


	/**
	 * Constructor. Creates the type of the window with the specified code
	 * and without parameter.
	 * @param code the code of the window
	 */
	private WindowType(int code) {
		this.code = code;
		this.parametrized = false;
		this.parameterDefault = 0.0;
		this.parameterMin = 0.0;
		this.parameterMax = 0.0;
	}

	/**
	 * Constructor.
	 * @param code the code of the window type
	 * @param parametrized boolean if the window function has a parameter
	 * @param parameterDefault the default value of the parameter
	 * @param parameterMin the minimal value of the parameter
	 * @param parameterMax the maximal value of the parameter
	 */
	private WindowType(int code, boolean parametrized, double parameterDefault, double parameterMin, double parameterMax) {
		this.code = code;
		this.parametrized = parametrized;
		this.parameterDefault = parameterDefault;
		this.parameterMin = parameterMin;
		this.parameterMax = parameterMax;
	}

	/**
	 * Returns the code of the window type.
	 * @return the code of the window type
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns if the window function has a parameter.
	 * @return {@code true} if the window function has a parameter,
	 * {@code false} otherwise
	 */
	public boolean isParametrized() {
		return parametrized;
	}

	/**
	 * Returns the default value of the parameter.
	 * @return the default value of the parameter
	 */
	public double getParameterDefault() {
		return parameterDefault;
	}

	/**
	 * Returns the minimal value of the parameter.
	 * @return the minimal value of the parameter
	 */
	public double getParameterMin() {
		return parameterMin;
	}

	/**
	 * Returns the maximal value of the parameter.
	 * @return the maximal value of the parameter
	 */
	public double getParameterMax() {
		return parameterMax;
	}

}
