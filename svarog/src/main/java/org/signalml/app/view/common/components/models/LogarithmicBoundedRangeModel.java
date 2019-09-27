package org.signalml.app.view.common.components.models;

import javax.swing.DefaultBoundedRangeModel;

/**
 * Model for sliders with logarithmic (non-linear) scale.
 *
 * Internally stored values (getMinimum / getMaximum / getValue) always return
 * an integer in range from 0 to 100.
 * Actual values can be accessed by getReal* and setReal* methods.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class LogarithmicBoundedRangeModel extends DefaultBoundedRangeModel {

	private double realMinimum;
	private double realMaximum;

	public LogarithmicBoundedRangeModel() {
		this(1.0, 1.0, Math.E);
	}

	public LogarithmicBoundedRangeModel(double realValue, double realMinimum, double realMaximum) {
		super(_calculateLogFromReal(realValue, realMinimum, realMaximum), 0, 0, 100);
		setRealMinimum(realMinimum);
		setRealMaximum(realMaximum);
	}

	public final double getRealMaximum() {
		return realMaximum;
	}

	public final double getRealMinimum() {
		return realMinimum;
	}
	
	public double getRealValue() {
		return calculateRealFromLog(getValue());
	}

	public final void setRealMaximum(double realMaximum) {
		if (realMaximum <= 0) {
			throw new RuntimeException("maximum value must be positive");
		}
		this.realMaximum = realMaximum;
		
	}

	public final void setRealMinimum(double realMinimum) {
		if (realMinimum <= 0) {
			throw new RuntimeException("minimum value must be positive");
		}
		this.realMinimum = realMinimum;
	}
	
	public void setRealRangeProperties(double realValue, double realMinimum, double realMaximum) {
		setRealMinimum(realMinimum);
		setRealMaximum(realMaximum);
		setRealValue(realValue);
	}

	public void setRealValue(double realValue) {
		if (realValue <= 0) {
			throw new RuntimeException("value must be positive");
		}
		setValue(calculateLogFromReal(realValue));
	}

	private int calculateLogFromReal(double real) {
		return _calculateLogFromReal(real, realMinimum, realMaximum);
	}
	
	private double calculateRealFromLog(int log) {
		return _calculateRealFromLog(log, realMinimum, realMaximum);
	}

	private static double _calculateRealFromLog(int log, double realMinimum, double realMaximum) {
		double logRealMinimum = Math.log(realMinimum);
		double real = Math.exp(log / 100.0
			* (Math.log(realMaximum) - logRealMinimum)
			+ logRealMinimum
		);
		real = Math.min(Math.max(real, realMinimum), realMaximum);
		return real;
	}

	private static int _calculateLogFromReal(double real, double realMinimum, double realMaximum) {
		double logRealMinimum = Math.log(realMinimum);
		real = Math.min(Math.max(real, realMinimum), realMaximum);
		return (int) Math.round(100.0
			* (Math.log(real) - logRealMinimum)
			/ (Math.log(realMaximum) - logRealMinimum)
		);
	}
}
