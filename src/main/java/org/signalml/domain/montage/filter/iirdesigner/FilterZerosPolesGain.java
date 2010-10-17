/* FilterZerosPolesGain.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import flanagan.complex.ComplexPoly;
import java.util.Arrays;

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

		ComplexPoly numeratorPoly;
		ComplexPoly denominatorPoly;
		Complex[] complexNumeratorCoeffs;
		Complex[] complexDenominatorCoeffs;
		double[] realNumeratorCoeffs;
		double[] realDenominatorCoeffs;

		if (getZeros().length > 0) {
			numeratorPoly = ComplexPoly.rootsToPoly(getZeros());
			complexNumeratorCoeffs = numeratorPoly.polyNomCopy();
		}
		else
			complexNumeratorCoeffs = new Complex[] {
			        new Complex(1.0, 0)
			};

		denominatorPoly = ComplexPoly.rootsToPoly(getPoles());
		complexDenominatorCoeffs = denominatorPoly.polyNomCopy();

		realNumeratorCoeffs = new double[complexNumeratorCoeffs.length];
		realDenominatorCoeffs = new double[complexDenominatorCoeffs.length];

		for (int i = 0; i < complexNumeratorCoeffs.length; i++)
			realNumeratorCoeffs[i] = gain * complexNumeratorCoeffs[complexNumeratorCoeffs.length - i - 1].getReal();
		for (int i = 0; i < complexDenominatorCoeffs.length; i ++)
			realDenominatorCoeffs[i] = complexDenominatorCoeffs[complexDenominatorCoeffs.length - i - 1].getReal();

		FilterCoefficients coeffs = new FilterCoefficients(realNumeratorCoeffs, realDenominatorCoeffs);
		return coeffs;

	}

	protected void print() {

		System.out.println("Zeros:");
		for (int i = 0; i < zeros.length; i++)
			zeros[i].println();

		System.out.println("Poles:");
		for (int i = 0; i < poles.length; i++)
			poles[i].println();

		System.out.println("Gain: "+gain);

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
