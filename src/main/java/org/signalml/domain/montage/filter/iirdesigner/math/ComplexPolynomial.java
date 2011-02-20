/* ComplexPolynomial.java created 2011-02-19
 *
 */

package org.signalml.domain.montage.filter.iirdesigner.math;

import org.apache.commons.math.complex.Complex;

/**
 *
 * @author Piotr Szachewicz
 */
public class ComplexPolynomial {

	private Complex[] coefficients;

	public ComplexPolynomial(Complex[] coefficients) {
		this.coefficients = coefficients.clone();
	}

	public ComplexPolynomial(double[] coefficients) {
		this.coefficients = new Complex[coefficients.length];
		for (int i = 0; i < coefficients.length; i++)
			this.coefficients[i] = new Complex(coefficients[i], 0.0);
	}

	public int getDegree() {
		return coefficients.length - 1;
	}

	public Complex[] getCoefficients() {
		return coefficients.clone();
	}

	protected Complex getCoefficient(int i) {
		return coefficients[i];
	}

	public Complex evaluate(Complex argument) {
		Complex value;
		value = coefficients[0];

		for (int i = 1; i < coefficients.length; i++)
			value = value.multiply(argument).add(coefficients[i]);

		return value;
	}

	public ComplexPolynomial multiply(ComplexPolynomial other) {
		Complex[] newCoefficients = new Complex[this.getDegree() + other.getDegree() + 1];

		for (int i = this.getDegree(); i >= 0; i--)
			for (int j = other.getDegree(); j >= 0; j--) {
				Complex factor = this.getCoefficient(i).multiply(other.getCoefficient(j));
				if (newCoefficients[i+j] == null)
					newCoefficients[i+j] = factor;
				else
					newCoefficients[i+j] = newCoefficients[i+j].add(factor);
			}

		return new ComplexPolynomial(newCoefficients);
	}

	public static ComplexPolynomial rootsToPolynomial(Complex[] roots) {
		ComplexPolynomial polynomial;
		ComplexPolynomial tempPolynomial;
		Complex[] tempCoefficients = new Complex[2];

		tempCoefficients[0] = new Complex(1.0, 0.0);
		tempCoefficients[1] = roots[0].multiply(new Complex(-1.0, 0.0));
		polynomial = new ComplexPolynomial(tempCoefficients);

		for (int i = 1; i < roots.length; i++) {
			tempCoefficients[1] = roots[i].multiply(new Complex(-1.0, 0.0));
			tempPolynomial = new ComplexPolynomial(tempCoefficients);
			polynomial = polynomial.multiply(tempPolynomial);
		}

		return polynomial;
	}

}
