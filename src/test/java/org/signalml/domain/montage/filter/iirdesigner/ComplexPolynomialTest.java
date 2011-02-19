/* ComplexPolynomialTest.java created 2011-02-19
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.apache.commons.math.complex.Complex;
import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;
import org.junit.Test;

/**
 *
 * @author Piotr Szachewicz
 */
public class ComplexPolynomialTest {

	@Test
	public void testGetDegree() {
		//test1
		Complex[] coefficients = new Complex[] {new Complex(1.0, 0.5), new Complex(0.0, 3.0)};
		ComplexPolynomial polynomial = new ComplexPolynomial(coefficients);

		assertEquals(1, polynomial.getDegree());

		//test2
		coefficients = new Complex[] {new Complex(0.0, 3.0)};
		polynomial = new ComplexPolynomial(coefficients);
		assertEquals(0, polynomial.getDegree());

		//test3
		coefficients = new Complex[] {new Complex(0.0, 3.0), new Complex(1.0, 3.0), new Complex(4.0, 3.0), new Complex(2.0, 2.0)};
		polynomial = new ComplexPolynomial(coefficients);
		assertEquals(3, polynomial.getDegree());
	}

	@Test
	public void testMultiplyPolynomialsReal() {
		ComplexPolynomial poly1 = new ComplexPolynomial(new Complex[] {new Complex(1.0, 0.0), new Complex(2.0, 0.0), new Complex(3.0, 0.0)});
		ComplexPolynomial poly2 = new ComplexPolynomial(new Complex[] {new Complex(4.0, 0.0), new Complex(3.0, 0.0), new Complex(3.0, 0.0)});

		ComplexPolynomial result = poly1.multiply(poly2);

		Complex[] coefficients = result.getCoefficients();
		Complex[] expected = new Complex[] {new Complex(4.0, 0.0), new Complex(11.0, 0.0), new Complex(21.0, 0.0),
		new Complex(15.0, 0.0), new Complex(9.0, 0.0)};
		for(int i = 0; i < coefficients.length; i++)
			equals(expected[i], coefficients[i]);

	}

	@Test
	public void testMultiplyPolynomialsComplex() {
		ComplexPolynomial poly1 = new ComplexPolynomial(new Complex[] {new Complex(1.0, 2.0), new Complex(3.0, 1.0), new Complex(0.5, 1.0)});
		ComplexPolynomial poly2 = new ComplexPolynomial(new Complex[] {new Complex(5.0, 1.0), new Complex(1.0, 1.0), new Complex(0.3, 0.2)});

		ComplexPolynomial result = poly1.multiply(poly2);

		Complex[] coefficients = result.getCoefficients();
		Complex[] expected = new Complex[] {new Complex(3.0, 11.0), new Complex(13.0, 11.0), new Complex(3.4, 10.3),
		new Complex(0.2, 2.4), new Complex(-0.05, 0.4)};
		for(int i = 0; i < coefficients.length; i++)
			equals(expected[i], coefficients[i]);

	}

	@Test
	public void testMultiplyPolynomialsComplex2() {
		ComplexPolynomial poly1 = new ComplexPolynomial(new Complex[] {new Complex(1.0, 0.0), new Complex(-1.0, -0.5)});
		ComplexPolynomial poly2 = new ComplexPolynomial(new Complex[] {new Complex(1.0, 0.0), new Complex(-2.0, -3.0)});

		ComplexPolynomial result = poly1.multiply(poly2);

		Complex[] coefficients = result.getCoefficients();
		Complex[] expected = new Complex[] {new Complex(1.0, 0.0), new Complex(-3.0, -3.5), new Complex(0.5, 4.0)};
		for(int i = 0; i < coefficients.length; i++)
			equals(expected[i], coefficients[i]);

	}

	public void equals(Complex c1, Complex c2) {
		assertEquals(c1.getReal(), c2.getReal(), 1e-6);
		assertEquals(c1.getImaginary(), c2.getImaginary(), 1e-6);
	}

	@Test
	public void testRootsToPolynomialReal() {
		Complex[] roots = new Complex[] {new Complex(2.0, 0.0)};
		ComplexPolynomial polynomial = ComplexPolynomial.rootsToPolynomial(roots);
		Complex[] coefficients = polynomial.getCoefficients();

		assertEquals(2, coefficients.length);
		equals(new Complex(1.0, 0.0), coefficients[0]);
		equals(new Complex(-2.0, 0.0), coefficients[1]);
	}

	@Test
	public void testRootsToPolynomialComplex() {
		Complex[] roots = new Complex[] {new Complex(1.0, 0.5)};
		ComplexPolynomial polynomial = ComplexPolynomial.rootsToPolynomial(roots);
		Complex[] coefficients = polynomial.getCoefficients();

		assertEquals(2, coefficients.length);
		equals(new Complex(1.0, 0.0), coefficients[0]);
		equals(new Complex(-1.0, -0.5), coefficients[1]);
	}

	@Test
	public void testRootsToPolynomialComplex2() {
		Complex[] roots = new Complex[] {new Complex(1.0, 0.5), new Complex(2.0, 3.0)};
		ComplexPolynomial polynomial = ComplexPolynomial.rootsToPolynomial(roots);
		Complex[] coefficients = polynomial.getCoefficients();

		assertEquals(3, coefficients.length);
		equals(new Complex(1.0, 0.0), coefficients[0]);
		equals(new Complex(-3.0, -3.5), coefficients[1]);
		equals(new Complex(0.5, 4.0), coefficients[2]);
	}

	@Test
	public void testRootsToPolynomialComplex3() {
		Complex[] roots = new Complex[] {new Complex(1.0, 0.5), new Complex(0.11, 0.43), new Complex(0.52, 0.964), new Complex(1.11, 2.22)};
		ComplexPolynomial polynomial = ComplexPolynomial.rootsToPolynomial(roots);
		Complex[] coefficients = polynomial.getCoefficients();

		Complex[] expected = new Complex[] {
			new Complex(1.000000, 0.0), new Complex(-2.740000, -4.114),
			new Complex(-2.819700, 7.75958), new Complex(5.518916, -1.47188),
			new Complex(-0.914751,-0.991563)
		};
		assertEquals(5, coefficients.length);
		for (int i = 0; i < expected.length; i++)
			equals(expected[i], coefficients[i]);
	}
}
