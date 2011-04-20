/* WindowType.java created 2007-12-16
 *
 */

package org.signalml.fft;

import org.springframework.context.MessageSourceResolvable;

/** WindowType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum WindowType implements MessageSourceResolvable {

	RECTANGULAR,
	BARTLETT,
	WELCH,
	HANN,
	HAMMING,
	KAISER(true, 2.0, Double.MIN_VALUE, Double.MAX_VALUE),
	GAUSSIAN(true, 2.5, 2.0, Double.MAX_VALUE)
	;

	private final boolean parametrized;
	private final double parameterDefault;
	private final double parameterMin;
	private final double parameterMax;

	private static final Object[] ARGUMENTS = new Object[0];

	private WindowType() {
		this.parametrized = false;
		this.parameterDefault = 0.0;
		this.parameterMin = 0.0;
		this.parameterMax = 0.0;
	}

	private WindowType(boolean parametrized, double parameterDefault, double parameterMin, double parameterMax) {
		this.parametrized = parametrized;
		this.parameterDefault = parameterDefault;
		this.parameterMin = parameterMin;
		this.parameterMax = parameterMax;
	}

	public boolean isParametrized() {
		return parametrized;
	}

	public double getParameterDefault() {
		return parameterDefault;
	}

	public double getParameterMin() {
		return parameterMin;
	}

	public double getParameterMax() {
		return parameterMax;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "fft.windowType." + name() };
	}

	@Override
	public String getDefaultMessage() {
		return name();
	}

}
