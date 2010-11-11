package org.signalml.plugin.export.signal;

/**
 * This class represents the type of samples in the file
 * with raw signal.
 * Samples can be represented as: double, float, int, short.
 * @author Marcin Szumski
 */
public enum ExportedRawSignalSampleType {

	/**
     * Samples of the length of 64 bits in the form of double
     */
	DOUBLE,
	/**
     * Samples of the length of 32 bits in the form of floats
     */
	FLOAT,
	/**
     * Samples of the length of 32 bits in the form of integers
     */
	INT,
	/**
     * Samples of the length of 16 bits in the form of integers
     */
	SHORT;
	
}
