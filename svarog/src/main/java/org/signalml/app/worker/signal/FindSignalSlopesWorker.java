package org.signalml.app.worker.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import org.signalml.app.model.tag.SlopeType;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * This worker is responsible for finding a specific type of {@link SlopeType slopes}
 * throught the signal.
 *
 * @author Piotr Szachewicz
 */
public class FindSignalSlopesWorker extends SwingWorkerWithBusyDialog<Integer[], Void> {

	private static final int BUFFER_SIZE = 1024;

	private SynchronizeTagsWithTriggerParameters parameters;
	private double threshold;
	private MultichannelSampleSource sampleSource;

	public FindSignalSlopesWorker(SynchronizeTagsWithTriggerParameters parameters) {
		super(null);
		this.parameters = parameters;
		this.threshold = parameters.getThresholdValue();
		this.sampleSource = parameters.getSampleSource();

		getBusyDialog().setText(_("Finding trigger activating slopes."));
		getBusyDialog().setCancellable(false);
	}

	@Override
	protected Integer[] doInBackground() throws Exception {
		showBusyDialog();

		List<Integer> slopePositions = new ArrayList<Integer>();
		int sampleCount = sampleSource.getSampleCount(0);
		int currentSample = 0;
		while(currentSample < sampleCount) {

			int bufferSize = BUFFER_SIZE;
			if (currentSample + bufferSize > sampleCount)
				bufferSize = sampleCount - currentSample;

			double[] samples = new double[bufferSize];

			sampleSource.getSamples(parameters.getTriggerChannel(), samples, currentSample, bufferSize, 0);

			int i = 0;
			for (; i < samples.length-1; i++) {
				if (isSlopeActivating(samples[i], samples[i+1])) {
					slopePositions.add(currentSample + i + 1);
				}
			}

			currentSample += i + 1;

		}

		return slopePositions.toArray(new Integer[0]);
	}

	protected boolean isSlopeActivating(double currentSample, double nextSample) {
		SlopeType slopeType = parameters.getSlopeType();

		switch(slopeType) {
		case ASCENDING: return isSlopeAscending(currentSample, nextSample);
		case DESCENDING: return isSlopeDescending(currentSample, nextSample);
		case BOTH:
			return isSlopeAscending(currentSample, nextSample)
				|| isSlopeDescending(currentSample, nextSample);
		}
		return false;

	}

	protected boolean isSlopeAscending(double currentSample, double nextSample) {
		return (nextSample >= threshold && currentSample < threshold);
	}

	protected boolean isSlopeDescending(double currentSample, double nextSample) {
		return (nextSample < threshold && currentSample >= threshold);
	}

}
