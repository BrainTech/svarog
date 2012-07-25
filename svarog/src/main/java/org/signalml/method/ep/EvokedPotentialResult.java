/* EvokedPotentialResult.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** EvokedPotentialResult
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private EvokedPotentialData data;

	private int channelCount;
	private int sampleCount;

	private float samplingFrequency;

	private String[] labels;
	private List<double[][]> averageSamples = new ArrayList<double[][]>();;

	private int skippedCount;
	private int averagedCount;

	private double secondsBefore;
	private double secondsAfter;

	public EvokedPotentialResult(EvokedPotentialData data) {
		this.data = data;
	}

	public EvokedPotentialData getData() {
		return data;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}

	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public double[][] getSingleChannelAverageSamples() {
		return averageSamples.get(0);
	}

	public List<double[][]> getAverageSamples() {
		return averageSamples;
	}

	public void setAverageSamples(double[][] averageSamples) {
		this.averageSamples = new ArrayList<double[][]>();
		this.averageSamples.add(averageSamples);
	}

	public void addAverageSamples(double[][] averageSamples) {
		this.averageSamples.add(averageSamples);
	}

	public int getSkippedCount() {
		return skippedCount;
	}

	public void setSkippedCount(int skippedCount) {
		this.skippedCount = skippedCount;
	}

	public int getAveragedCount() {
		return averagedCount;
	}

	public void setAveragedCount(int averagedCount) {
		this.averagedCount = averagedCount;
	}

	public double getSecondsBefore() {
		return secondsBefore;
	}

	public void setSecondsBefore(double secondsBefore) {
		this.secondsBefore = secondsBefore;
	}

	public double getSecondsAfter() {
		return secondsAfter;
	}

	public void setSecondsAfter(double secondsAfter) {
		this.secondsAfter = secondsAfter;
	}

}
