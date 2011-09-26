/* InitialStateCalculator.java created 2010-03-01
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;


/**
 * This class calculates the initial state parameters for a filter.
 * These initial state can be used to perform filtfilt with
 * minimal distortion at the boundaries of the signal.
 *
 * @author Piotr Szachewicz
 */
public class InitalStateCalculator {
	/**
	 * Implemented as in http://www.scipy.org/Cookbook/FiltFilt (Python).
	 */

	/**
	 * the feedforward coefficients of a filter
	 */
	private double[] bCoefficients;

	/**
	 * the feedback coefficients of a filter
	 */
	private double[] aCoefficients;

	/**
	 * the maximum number of coefficients in both arrays
	 */
	private int numberOfCoefficients;

	/**
	 * an array containing the inital state of the signal
	 */
	private double[] initialState;

	/**
	 * Constructor
	 * @param filterCoefficients the coefficients of the filter for which
	 * this calculator calculates the initial state
	 */
	public InitalStateCalculator(FilterCoefficients filterCoefficients) {
		this.aCoefficients = filterCoefficients.getACoefficients();
		this.bCoefficients = filterCoefficients.getBCoefficients();
		makeCoefficientArraysEqualLength();
		numberOfCoefficients = aCoefficients.length; //both arrays have the same length now
		initialState = calculateInitialState();
	}

	/**
	 * If arrays are of different lengths, this method expands the shorter
	 * array with zeros so that both are of equal length.
	 */
	private void makeCoefficientArraysEqualLength() {
		int maxLength = Math.max(aCoefficients.length, bCoefficients.length);

		bCoefficients = ArrayOperations.padArrayWithZerosToSize(bCoefficients, maxLength);
		aCoefficients = ArrayOperations.padArrayWithZerosToSize(aCoefficients, maxLength);
	}

	/**
	 * Calculates the initial state.
	 * @return the initial state
	 */
	private double[] calculateInitialState() {
		RealMatrix zin = calculateZIN();
		RealVector zid = calculateZID();
		RealVector zi = getInvertedMatrix(zin).operate(zid);

		return zi.getData();
	}

	/**
	 * Returns the initial state for the filter.
	 * @return the initial state for the given filter
	 */
	public double[] getInitialState() {
		return initialState;
	}

	/**
	 * Returns the inverse of the given matrix.
	 * @param matrix a matrix for which the inverse is calculated
	 * @return the inverse of the given matrix
	 */
	protected RealMatrix getInvertedMatrix(RealMatrix matrix) {
		return new LUDecompositionImpl(matrix).getSolver().getInverse();
	}

	/**
	 * Calculates the ZID vector. (Compare the python code).
	 * @return the ZID vector
	 */
	protected RealVector calculateZID() {
		double[] bSubArray = new double[numberOfCoefficients-1];
		double[] aSubArray = new double[numberOfCoefficients-1];

		System.arraycopy(bCoefficients, 1, bSubArray, 0, bSubArray.length);
		System.arraycopy(aCoefficients, 1, aSubArray, 0, aSubArray.length);

		RealVector bVector = new ArrayRealVector(bSubArray);
		RealVector aVector = new ArrayRealVector(aSubArray);

		RealVector zid = bVector.subtract(aVector.mapMultiply(bCoefficients[0]));
		return zid;
	}

	/**
	 * Calculates the ZIN matrix (compare the python code).
	 * @return the ZIN matrix
	 */
	protected RealMatrix calculateZIN() {
		RealMatrix identityMatrix = MatrixUtils.createRealIdentityMatrix(numberOfCoefficients-1);
		RealMatrix subtrahend = calculateSubtrahendForZIN();

		return identityMatrix.subtract(subtrahend);
	}

	/**
	 * Calculates the subtrahend used to calculate the ZIN matrix.
	 * @return the subtrahend
	 */
	protected RealMatrix calculateSubtrahendForZIN() {
		int matrixSize = numberOfCoefficients - 1;
		double[][] subtrahendData = new double[matrixSize][matrixSize];

		/* first column are the a coefficients (omitting the first one)
		 * multiplied by -1.
		 */
		for (int i = 0; i < matrixSize; i++) {
			subtrahendData[i][0] = -1 * aCoefficients[i+1];
		}

		for (int row = 0; row < matrixSize-1; row++)
			subtrahendData[row][row+1] = 1;

		return new Array2DRowRealMatrix(subtrahendData);
	}

	/**
	 * Grows the signal to have edges for stabilizing the filter with
	 * inverted replicas of the signal.
	 * @param signal the signal to be grown.
	 * @return the grown version of the signal
	 */
	public double[] growSignal(double[] signal) {
		int ntaps = Math.max(aCoefficients.length, bCoefficients.length);
		int edge = ntaps * 3 - 1;

		int grownSignalSize = edge + signal.length + edge;
		double[] grownSignal = new double[grownSignalSize];

		System.arraycopy(signal, 0, grownSignal, edge, signal.length);

		for (int i = 0; i < edge; i++) {
			grownSignal[i] = 2 * signal[0] - signal[edge - i];
		}

		for (int i = 0; i < edge; i++) {
			grownSignal[edge + signal.length + i] = 2 * signal[signal.length - 1]
				- signal[signal.length - 2 - i];
		}

		return grownSignal;
	}

}
