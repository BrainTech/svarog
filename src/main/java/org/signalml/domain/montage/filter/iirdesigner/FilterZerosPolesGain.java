/* FilterZerosPolesGain.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.apache.commons.math.complex.Complex;
import java.util.Arrays;
import org.signalml.domain.montage.filter.iirdesigner.math.ComplexPolynomial;

/**
 * This class contains the values the ZPK (zeros, poles and gain) representation of a filter.
 *
 * @author Piotr Szachewicz
 */
class FilterZerosPolesGain {

	/**
	 * an array of zeros (the roots of the numerator of the transfer function representing the filter)
	 */
	Complex[] zeros;

	/**
	 * an array of poles (the roots of the denominator of the transfer function representing the filter)
	 */
	Complex[] poles;

	/**
	 * the value of the filter's gain
	 */
	double gain;

	/**
	 * Constructor. Creates a ZPK filter representation using the given values.
	 *
	 * @param zeros an array of zeros
	 * @param poles an array of poles
	 * @param gain the value of gain
	 */
	protected FilterZerosPolesGain(Complex[] zeros, Complex[] poles, double gain) {

		this.zeros = zeros.clone();
		this.poles = poles.clone();
		this.gain = gain;

	}

	/**
	 * Returs an array of zeros stored in this filter representation.
	 *
	 * @return an array of zeros
	 */
	protected Complex[] getZeros() {
		return zeros;
	}

	/**
	 * Returns an array of poles stored in this filter representation.
	 *
	 * @return an array of poles
	 */
	protected Complex[] getPoles() {
		return poles;
	}

	/**
	 * Returns the value of gain stored in this filter representation.
	 *
	 * @return the value of gain
	 */
	protected double getGain() {
		return gain;
	}

	/**
	 * Converts this zero, poles, gain representation of a filter to an
	 * equivalent b, a coefficients (feedforward, feedback coefficients)
	 * {@link FilterCoefficients representation}.
	 *
	 * @return the {@link FilterCoefficients} representation of this filter.
	 */
	protected FilterCoefficients convertToBACoefficients() {

		ComplexPolynomial numeratorPoly;
		ComplexPolynomial denominatorPoly;
		Complex[] complexNumeratorCoeffs;
		Complex[] complexDenominatorCoeffs;
		double[] realNumeratorCoeffs;
		double[] realDenominatorCoeffs;

		if (getZeros().length > 0) {
			numeratorPoly = ComplexPolynomial.rootsToPolynomial(getZeros());
			complexNumeratorCoeffs = numeratorPoly.getCoefficients();
		}
		else
			complexNumeratorCoeffs = new Complex[] {
			        new Complex(1.0, 0)
			};

		denominatorPoly = ComplexPolynomial.rootsToPolynomial(getPoles());
		complexDenominatorCoeffs = denominatorPoly.getCoefficients();

		realNumeratorCoeffs = new double[complexNumeratorCoeffs.length];
		realDenominatorCoeffs = new double[complexDenominatorCoeffs.length];

		for (int i = 0; i < complexNumeratorCoeffs.length; i++)
			realNumeratorCoeffs[i] = gain * complexNumeratorCoeffs[i].getReal();
		for (int i = 0; i < complexDenominatorCoeffs.length; i ++)
			realDenominatorCoeffs[i] = complexDenominatorCoeffs[i].getReal();

		FilterCoefficients coeffs = new FilterCoefficients(realNumeratorCoeffs, realDenominatorCoeffs);
		return coeffs;

	}

	/**
	 * Prints the poles, zeros and gain on screen.
	 */
	protected void print() {
		System.out.println(toString());
	}

	/**
	 * Returns a string containing the values of poles, zeros and gain.
	 * @return a string containing the values of zeros, poles and gain.
	 */
	@Override
	public String toString() {

		String s;
		s = "Zeros:\n";
		for (int i = 0; i < zeros.length; i++)
			s += ("     " + zeros[i].toString() + "\n");

		s += "Poles:\n";
		for (int i = 0; i < poles.length; i++)
			s += ("     " + poles[i] + "\n");

		s += ("Gain: " + gain);

		return s;
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof FilterZerosPolesGain))
			return false;
		FilterZerosPolesGain zpk = (FilterZerosPolesGain) o;

		if (zeros.length == zpk.getZeros().length)
			for (int i = 0; i < zeros.length; i++)
				if (!zeros[i].equals(zpk.getZeros()[i]))
					return false;
				else
					return false;

		if (poles.length == zpk.getPoles().length)
			for (int i = 0; i < poles.length; i++)
				if (!poles[i].equals(zpk.getPoles()[i]))
					return false;
				else
					return false;

		if (gain!=zpk.getGain())
			return false;
		return true;

	}

	@Override
	public int hashCode() {

		int hash = 3;
		hash = 67 * hash + Arrays.deepHashCode(this.zeros);
		hash = 67 * hash + Arrays.deepHashCode(this.poles);
		hash = 67 * hash + (int) (Double.doubleToLongBits(this.gain) ^ (Double.doubleToLongBits(this.gain) >>> 32));
		return hash;

	}

}
