/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * This class holds information about the newest samples package that was received
 * by Svarog and published by the doInTheBackground method.
 *
 * The data consists of the sample values (a sample value for each channel)
 * and a timestamp of these samples.
 * @author Piotr Szachewicz
 */
public class NewSamplesData {

	/**
	 * The values of the samples. The size of the array is equal to the number
	 * of channels in the signal.
	 */
	private float[] sampleValues;
	/**
	 * The values of the impedance. The size of the array is equal to the number
	 * of channels with impedance in the signal.
	 */
	private Map<Integer, Float> sampleImpedance;
	/**
	 * The values of the impedance flags. The size of the array is equal to the number
	 * of channels in the signal.
	 */
	private int[] sampleImpedanceFlags;
	/**
	 * The timestamp of the samples represented by the sampleValues array.
	 */
	private double samplesTimestamp;

	/**
	 * Constructor. Creates an object containing samples data.
	 * @param sampleValues the values of the samples for each channel
	 * @param sampleImpedanceFlags the values of the samples impedance for each channel
	 * @param samplesTimestamp the timestamp of the samples
	 */
	public NewSamplesData(float[] sampleValues, int[] sampleImpedanceFlags, double samplesTimestamp) {
		this.sampleValues = sampleValues;
		this.samplesTimestamp = samplesTimestamp;
		this.sampleImpedanceFlags = sampleImpedanceFlags;
		assert !IntStream.of(this.sampleImpedanceFlags
			).anyMatch(flag -> flag == 2) : "None IMPEDANCE_PRESENT flags should be present."; // 2-> PRESENT
		this.sampleImpedance = null;
	}
	/**
	 * Constructor. Creates an object containing samples data.
	 * @param sampleValues the values of the samples for each channel
	 * @param sampleImpedanceFlags the values of the samples impedance flags for each channel
	 * @param sampleImpedance the values of the samples impedance for each channel with impedance
	 * @param samplesTimestamp the timestamp of the samples
	 */
	public NewSamplesData(
			float[] sampleValues,
			int[] sampleImpedanceFlags,
			Map<Integer, Float> sampleImpedance,
			double samplesTimestamp
	) {
		this.sampleValues = sampleValues;
		this.samplesTimestamp = samplesTimestamp;
		this.sampleImpedanceFlags = sampleImpedanceFlags;
		assert IntStream.of(this.sampleImpedanceFlags
			).anyMatch(flag -> flag == 2): "At least 1 IMPEDANCE_PRESENT flag should be present."; // 2-> PRESENT
		assert !sampleImpedance.isEmpty() : "Impedance map shouldn't be empty when IMPEDANCE_PRESENT flag is present";
		this.sampleImpedance = sampleImpedance;
	}

	float[] getSampleValues() { return sampleValues; }

	int[] getSampleImpedanceFlags() { return sampleImpedanceFlags; }

	Map<Integer, Float> getSampleImpedance() { return sampleImpedance; }

	double getSamplesTimestamp() {
		return samplesTimestamp;
	}

}
