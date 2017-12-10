/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor;

import java.util.Map;

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
	 * The values of the impedance and timestamps.
	 */
	private Impedance.ImpedanceData sampleImpedance;
	/**
	 * The timestamp of the samples represented by the sampleValues array.
	 */
	private double samplesTimestamp;

	/**
	 * Constructor. Creates an object containing samples data.
	 * @param sampleValues the values of the samples for each channel
	 * @param sampleImpedance the values of the samples impedance with flags for each channel with impedance
	 * @param samplesTimestamp the timestamp of the samples
	 */
	public NewSamplesData(
			float[] sampleValues,
			Impedance.ImpedanceData sampleImpedance,
			double samplesTimestamp
	) {
		this.sampleValues = sampleValues;
		this.samplesTimestamp = samplesTimestamp;
		this.sampleImpedance = sampleImpedance;
	}

	float[] getSampleValues() { return sampleValues; }

	int[] getSampleImpedanceFlags() { return sampleImpedance.flags; }

	Map<Integer, Float> getSampleImpedance() { return sampleImpedance.data; }

	double getSamplesTimestamp() {
		return samplesTimestamp;
	}

}
