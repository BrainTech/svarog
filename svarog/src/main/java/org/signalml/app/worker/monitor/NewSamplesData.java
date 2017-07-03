/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor;

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
	 * The timestamp of the samples represented by the sampleValues array.
	 */
	private double samplesTimestamp;

	/**
	 * Constructor. Creates an object containing samples data.
	 * @param sampleValues the values of the samples for each channel
	 * @param samplesTimestamp the timestamp of the samples
	 */
	public NewSamplesData(float[] sampleValues, double samplesTimestamp) {
		this.sampleValues = sampleValues;
		this.samplesTimestamp = samplesTimestamp;
	}

	public float[] getSampleValues() {
		return sampleValues;
	}

	public void setSampleValues(float[] sampleValues) {
		this.sampleValues = sampleValues;
	}

	public double getSamplesTimestamp() {
		return samplesTimestamp;
	}

	public void setSamplesTimestamp(double samplesTimestamp) {
		this.samplesTimestamp = samplesTimestamp;
	}
	
}
