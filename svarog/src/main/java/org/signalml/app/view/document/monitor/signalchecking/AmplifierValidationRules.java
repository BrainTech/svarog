package org.signalml.app.view.document.monitor.signalchecking;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * Contains information about signal validation methods for a given amplifier.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierValidationRules {

	/**
	 * Amplifier's name
	 */
	private String amplifierName;

	/**
	 * Delay between checks in miliseconds.
	 */
	private int delay;

	/**
	 * Signal checking methods - a HashMap, the key is method type
	 * and value is given method's parameters.
	 */
	private EnumMap<SignalCheckingMethod, HashMap<String, Object>> methods;

	/**
	 * Returns the amplifier name.
	 * @return the amplifier name
	 */
	public String getAmplifierName() {
		return amplifierName;
	}

	/**
	 * Returns signal checking methods and parameters.
	 * @return signal checking methods and parameters
	 */
	public EnumMap<SignalCheckingMethod, HashMap<String, Object>> getMethods() {
		return methods;
	}

	/**
	 * Returns the delay.
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Default constructor.
	 * @param amplifierName {@link #amplifierName}
	 * @param methods {@link #methods}
	 * @param delay {@link #delay}
	 */
	public AmplifierValidationRules(String amplifierName, EnumMap<SignalCheckingMethod, HashMap<String, Object>> methods, int delay) {
		this.amplifierName = amplifierName;
		this.methods = methods;
		this.delay = delay;
	}
}
