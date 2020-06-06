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

	/**
	 * Channel labels.
	 */
	private String[] labels;
	private List<double[][]> averageSamples = new ArrayList<>();;

	private List<Integer> unusableSegmentsCount = new ArrayList<>();
	private List<Integer> averagedSegmentsCount = new ArrayList<>();
	private List<Integer> artifactRejectedSegmentsCount = new ArrayList<>();

	private double startTime;
	private double segmentLength;

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

	public List<double[][]> getAverageSamples() {
		return averageSamples;
	}

	public void setAverageSamples(double[][] averageSamples) {
		this.averageSamples = new ArrayList<>();
		this.averageSamples.add(averageSamples);
	}

	public void addAverageSamples(double[][] averageSamples) {
		this.averageSamples.add(averageSamples);
	}

	public List<Integer> getUnusableSegmentsCount() {
		return unusableSegmentsCount;
	}

	public void setUnusableSegmentsCount(List<Integer> unusableSegmentsCount) {
		this.unusableSegmentsCount = unusableSegmentsCount;
	}

	public List<Integer> getAveragedSegmentsCount() {
		return averagedSegmentsCount;
	}

	public void setAveragedSegmentsCount(List<Integer> averagedSegmentsCount) {
		this.averagedSegmentsCount = averagedSegmentsCount;
	}

	public List<Integer> getArtifactRejectedSegmentsCount() {
		return artifactRejectedSegmentsCount;
	}

	public void setArtifactRejectedSegmentsCount(List<Integer> artifactRejectedSegmentsCount) {
		this.artifactRejectedSegmentsCount = artifactRejectedSegmentsCount;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(double segmentLength) {
		this.segmentLength = segmentLength;
	}

}
