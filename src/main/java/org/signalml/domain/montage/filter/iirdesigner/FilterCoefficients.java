/* FilterCoefficients.java created 2010-09-12
 *
 */
package org.signalml.domain.montage.filter.iirdesigner;

import org.signalml.domain.montage.filter.iirdesigner.math.SpecialMath;
import java.util.Arrays;

/**
 * This class contains the values of the feedback and feedforward coefficients (B & A coefficients)
 * of a particular filter.
 *
 * @author Piotr Szachewicz
 */
public class FilterCoefficients {

	/**
	 * an array of feedforward coefficients
	 */
	private double[] bCoefficients;

	/**
	 * an array of feeback coefficients
	 */
	private double[] aCoefficients;

	/**
	 * Constructor. Creates an instance of {@link FilterCoefficients} which will
	 * store given coefficients.
	 *
	 * @param bCoefficients an array of feedback coefficients
	 * @param aCoefficients an array of feedforward coefficients
	 */
	public FilterCoefficients(double[] bCoefficients, double[] aCoefficients) {

		this.aCoefficients = aCoefficients.clone();
		this.bCoefficients = bCoefficients.clone();

	}

	/**
	 * Returs the order of the filter represented by the feedback and feedforward
	 * coefficients stored in the {@link FilterCoefficients}
	 *
	 * @return the order orf the filter
	 */
	public int getFilterOrder() {
		return Math.max(aCoefficients.length - 1, bCoefficients.length - 1);
	}

	public int getNumberOfCoefficients() {
		return Math.max(aCoefficients.length, bCoefficients.length);
	}

	/**
	 * Returs an array of feedback coefficients.
	 *
	 * @return an array of feedback coefficients
	 */
	public double[] getACoefficients() {
		return aCoefficients;

	}

	/**
	 * Returns an array of feedforward coefficients.
	 *
	 * @return an array of feedforward coefficients.
	 */
	public double[] getBCoefficients() {
		return bCoefficients;
	}

	/**
	 * Returs whether the order of the filter represented by the coefficients stored
	 * in the {@link FilterCoefficients} is odd.
	 *
	 * @return true if the order of the filter is odd, false otherwise
	 */
	protected boolean isOddOrder() {

		if (getFilterOrder() % 2 == 1)
			return true;
		return false;

	}

	/**
	 * Normalizes the coefficients stored in the {@link FilterCoefficients} so
	 * that the first feedback coefficient is equal to 1.0.
	 */
	protected void normalize() throws BadFilterParametersException {

		int i;

		//finding the first non zero A Coefficient
		i = 0;
		while (i < aCoefficients.length && aCoefficients[i] == 0)
			i++;

		int firstNonZero = i;

		//deleting zero coefficients padding from aCoefficients
		double[] newACoefficients = new double[aCoefficients.length - firstNonZero];
		for (i = 0; i < newACoefficients.length; i++)
			newACoefficients[i] = aCoefficients[firstNonZero + i];

		aCoefficients = newACoefficients;

		//calculating normalized coefficients values
		double x = 1.0 / aCoefficients[0];
		for (i = 0; i < bCoefficients.length; i++)
			bCoefficients[i] = bCoefficients[i] * x;


		for (i = 0; i < aCoefficients.length; i++)
			aCoefficients[i] = aCoefficients[i] * x;


		if (Math.abs(bCoefficients[0]) <= 1e-8) {
			i = 0;
			while (i < bCoefficients.length && Math.abs(bCoefficients[i]) <= 1e-8)
				i++;
			int firstNotCloseToZero = i;

			double[] newBCoefficients = new double[bCoefficients.length - firstNotCloseToZero];
			for (i = 0; i < newBCoefficients.length; i++)
				newBCoefficients[i] = bCoefficients[firstNotCloseToZero + i];
			bCoefficients = newBCoefficients;

			throw new BadFilterParametersException("Badly conditioned filter coefficients (numerator): the results may be meaningless");
		}

	}

	/**
	 * If the the filter represented by the coefficients is a lowpass prototype,
	 * this function transforms it to a lowpass filter with a given cutoff frequency.
	 *
	 * @param w0 the cutoff frequency
	 */
	protected void transformLowpassToLowpass(double w0) throws BadFilterParametersException {

		int numberOfCoeffs = getNumberOfCoefficients();
		double[] powers = new double[numberOfCoeffs];
		int i;

		for (i = 0; i < numberOfCoeffs; i++)
			powers[i] = Math.pow(w0, numberOfCoeffs - 1 - i);

		double[] a = getACoefficients();
		double[] b = getBCoefficients();

		int start1 = Math.max(b.length - a.length, 0);
		int start2 = Math.max(a.length - b.length, 0);

		for (i = 0; i < b.length; i++)
			b[i] = b[i] * powers[start1] / powers[start2 + i];
		for (i = 0; i < a.length; i++)
			a[i] = a[i] * powers[start1] / powers[start1 + i];

		bCoefficients = b;
		aCoefficients = a;
		normalize();

	}

	/**
	 * If the the filter represented by the coefficients is a lowpass prototype,
	 * this function transforms it to a highpass filter with a given cutoff frequency.
	 *
	 * @param w0 the cutoff frequency
	 */
	protected void transformLowpassToHighpass(double w0) throws BadFilterParametersException {

		int numberOfCoeffs = getNumberOfCoefficients();
		double[] powers = new double[numberOfCoeffs];
		int i;

		for (i = 0; i < numberOfCoeffs; i++) {

			if (w0 != 1)
				powers[i] = Math.pow(w0, i);
			else
				powers[i] = 1.0;

		}

		double[] outa;
		double[] outb;

		if (aCoefficients.length >= bCoefficients.length) {

			outa = new double[aCoefficients.length];
			for (i = 0; i < aCoefficients.length; i++)
				outa[i] = aCoefficients[aCoefficients.length - i - 1] * powers[i];

			outb = new double[aCoefficients.length];
			for (i = 0; i < bCoefficients.length; i++)
				outb[i] = bCoefficients[bCoefficients.length - i - 1] * powers[i];
			for (i = bCoefficients.length; i < aCoefficients.length; i++)
				outb[i] = 0.0;

		}
		else {

			outb = new double[bCoefficients.length];
			for (i = 0; i < bCoefficients.length; i++)
				outb[i] = bCoefficients[bCoefficients.length - i - 1] * powers[i];

			outa = new double[bCoefficients.length];
			for (i = 0; i < aCoefficients.length; i++)
				outa[i] = aCoefficients[aCoefficients.length - i - 1] * powers[i];
			for (i = aCoefficients.length; i < bCoefficients.length; i++)
				outb[i] = 0.0;

		}

		aCoefficients = outa;
		bCoefficients = outb;
		normalize();

	}

	/**
	 * If the the filter represented by the coefficients is a lowpass prototype,
	 * this function transforms it to a bandpass filter with a given cutoff
	 * frequency and bandwidth.
	 *
	 * @param w0 the cutoff frequency
	 * @param bw the bandwidth of the bandpass filter
	 */
	protected void transformLowpassToBandpass(double w0, double bw) throws BadFilterParametersException {

		int D = aCoefficients.length - 1;
		int N = bCoefficients.length - 1;
		int ma = Math.max(D,N);
		int Np = N + ma;
		int Dp = D + ma;

		double[] bprime = new double[Np + 1];
		double[] aprime = new double[Dp + 1];

		double value;
		int i, j, k;
		for (j = 0; j <= Np; j++) {

			value = 0.0;
			for (i = 0; i <= N; i++)
				for (k = 0; k <= i; k++)
					if (ma - i + 2 * k == j)
						value += SpecialMath.combinations(i, k) * bCoefficients[N - i] * Math.pow(w0 * w0, i - k) / Math.pow(bw, i);
			bprime[Np - j] = value;

		}

		for (j = 0; j <= Dp; j++) {

			value = 0.0;
			for (i = 0; i <= D; i++)
				for (k = 0; k <= i; k++)
					if (ma - i + 2 * k == j)
						value += SpecialMath.combinations(i, k) * aCoefficients[D - i] * Math.pow(w0 * w0, i - k) / Math.pow(bw, i);
			aprime[Dp - j] = value;

		}

		aCoefficients = aprime;
		bCoefficients = bprime;
		normalize();

	}

	/**
	 * If the the filter represented by the coefficients is a lowpass prototype,
	 * this function transforms it to a band-stop filter with a given cutoff
	 * frequency and bandwidth.
	 *
	 * @param w0 the cutoff frequency
	 * @param bw the bandwidth of the bandstop filter
	 */
	protected void transformFromLowpassToBandstop(double w0, double bw) throws BadFilterParametersException {

		int D = aCoefficients.length - 1;
		int N = bCoefficients.length - 1;
		int ma = Math.max(D,N);
		int Np = ma + ma;
		int Dp = ma + ma;

		double[] bprime = new double[Np + 1];
		double[] aprime = new double[Dp + 1];

		double value;
		int i, j, k;

		for (j = 0; j <= Np; j++) {
			value = 0.0;
			for (i = 0; i <= N; i++)
				for (k = 0; k <= ma-i; k++)
					if (i + 2 * k == j)
						value += SpecialMath.combinations(ma - i, k) * bCoefficients[N - i] * Math.pow(w0 * w0, ma - i - k) * Math.pow(bw, i);
			bprime[Np - j] = value;
		}

		for (j = 0; j <= Dp; j++) {
			value = 0.0;
			for (i = 0; i <= D; i++)
				for (k = 0; k <= ma-i; k++)
					if (i + 2 * k == j)
						value += SpecialMath.combinations(ma - i, k) * aCoefficients[D - i] * Math.pow(w0 * w0, ma - i - k) * Math.pow(bw, i);
			aprime[Dp - j] = value;
		}

		aCoefficients = aprime;
		bCoefficients = bprime;
		normalize();

	}

	/**
	 * If the filter represented by the coefficients is an analog filter,
	 * this function transforms it to a digital filter using the bilinear
	 * transform.
	 *
	 * @param samplingFrequency the sampling frequency under which the digital
	 * filter will operate.
	 */
	protected void bilinearTransform(double samplingFrequency) throws BadFilterParametersException {

		int D = aCoefficients.length - 1;
		int N = bCoefficients.length - 1 ;
		int M = Math.max(D, N);
		int Dp = M;
		int Np = M;

		double[] bprime = new double[Np + 1];
		double[] aprime = new double[Dp + 1];

		double value;
		int i, j, k, l;

		for (j = 0; j <= Np; j++) {

			value = 0.0;
			for (i = 0; i <= N; i++)
				for (k = 0; k <= i; k++)
					for (l = 0; l <= M - i; l++)
						if (k + l == j)
							value += SpecialMath.combinations(i, k) * SpecialMath.combinations(M - i, l) * bCoefficients[N - i] * Math.pow(2.0 * samplingFrequency, i) * Math.pow(-1.0, k);
			bprime[j] = value;

		}

		for (j = 0; j <= Dp; j++) {

			value = 0.0;
			for (i = 0; i <= D; i++)
				for (k = 0; k <= i; k++)
					for (l = 0; l <= M - i; l++)
						if (k + l == j)
							value += SpecialMath.combinations(i, k) * SpecialMath.combinations(M - i, l) * aCoefficients[D - i] * Math.pow(2.0 * samplingFrequency, i) * Math.pow(-1.0, k);
			aprime[j] = value;

		}

		aCoefficients = aprime;
		bCoefficients = bprime;

		normalize();

	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof FilterCoefficients))
			return false;

		FilterCoefficients coefs = (FilterCoefficients)o;

		if (Arrays.equals(this.aCoefficients, coefs.aCoefficients)&&Arrays.equals(this.bCoefficients, coefs.bCoefficients))
			return true;
		return false;

	}

	@Override
	public int hashCode() {

		int hash = 7;
		hash = 59 * hash + Arrays.hashCode(this.aCoefficients);
		hash = 59 * hash + Arrays.hashCode(this.bCoefficients);
		return hash;

	}

	public void print() {

		System.out.println("filter order: " + getFilterOrder());

		System.out.print("b: ");
		for (int i = 0; i < bCoefficients.length; i++)
			System.out.print(bCoefficients[i] + ", ");
		System.out.println();

		System.out.print("a: ");
		for (int i = 0; i < aCoefficients.length; i++)
			System.out.print(aCoefficients[i] + ", ");
		System.out.println();
	}

	/**
	 * Returns a String containing information about b and a coefficients.
	 * @return a String describing these filter coefficients.
	 */
	@Override
	public String toString() {
		String s = "filter order: " + getFilterOrder();
		s += "\n";

		s += "b coefficients:\n";
		for (int i = 0; i < bCoefficients.length; i++)
			s += ("     " +bCoefficients[i] + "\n");

		s += "a coefficients:\n";
		for (int i = 0; i < aCoefficients.length; i++)
			s += ("     " + aCoefficients[i] + "\n");
		return s;
	}

}
