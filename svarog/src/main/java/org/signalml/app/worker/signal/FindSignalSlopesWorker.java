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
	private int lengthThreshold;

	public FindSignalSlopesWorker(SynchronizeTagsWithTriggerParameters parameters) {
		super(null);
		this.parameters = parameters;
		this.threshold = parameters.getThresholdValue();
		this.sampleSource = parameters.getSampleSource();
		this.lengthThreshold = (int) (parameters.getLengthThresholdValue() * sampleSource.getSamplingFrequency());

		getBusyDialog().setText(_("Finding trigger activating slopes."));
		getBusyDialog().setCancellable(false);
	}

	@Override
	protected Integer[] doInBackground() throws Exception {
		showBusyDialog();

		List<TriggerPush> triggerPushes = new ArrayList<TriggerPush>();
		int sampleCount = sampleSource.getSampleCount(0);
		int currentSample = 0;

		int triggerAsc = -1;
		int triggerDesc = -1;
		while(currentSample < sampleCount) {

			int bufferSize = BUFFER_SIZE;
			if (currentSample + bufferSize > sampleCount)
				bufferSize = sampleCount - currentSample;

			double[] samples = new double[bufferSize];

			sampleSource.getSamples(parameters.getTriggerChannel(), samples, currentSample, bufferSize, 0);

			int i = 0;
			for (; i < samples.length-1; i++) {
				double currentSampleValue = samples[i];
				double nextSampleValue = samples[i+1];
				if (isSlopeAscending(currentSampleValue, nextSampleValue))
					triggerAsc = currentSample + i + 1;
				else if (isSlopeDescending(currentSampleValue, nextSampleValue)) {
					triggerDesc = currentSample + i + 1;

					if (triggerAsc == -1)
						// if the first slope is descending
						triggerAsc = 0;

					TriggerPush triggerPush = new TriggerPush(triggerAsc, triggerDesc);
					triggerPushes.add(triggerPush);
					triggerAsc = -1;
					triggerDesc = -1;
				}
			}

			currentSample += i + 1;

		}

		if (triggerAsc != -1 && triggerDesc == -1) {
			// if the last push does not end
			triggerDesc = sampleCount-1;
			TriggerPush triggerPush = new TriggerPush(triggerAsc, triggerDesc);
			triggerPushes.add(triggerPush);
		}



		List<Integer> slopePositions = convertTriggerPushesToSlopePositions(triggerPushes);
		return slopePositions.toArray(new Integer[0]);
	}

	protected boolean isSlopeAscending(double currentSample, double nextSample) {
		return (nextSample >= threshold && currentSample < threshold);
	}

	protected boolean isSlopeDescending(double currentSample, double nextSample) {
		return (nextSample < threshold && currentSample >= threshold);
	}

	protected List<Integer> convertTriggerPushesToSlopePositions(List<TriggerPush> triggerPushes) {
		List<Integer> slopePositions = new ArrayList<Integer>();

		for (TriggerPush triggerPush: triggerPushes) {
			if (parameters.isLengthThresholdEnabled() && triggerPush.getLength() < lengthThreshold)
				continue;

			switch(parameters.getSlopeType()) {
			case ASCENDING:
				slopePositions.add(triggerPush.getStartSample());
				break;
			case BOTH:
				slopePositions.add(triggerPush.getStartSample());
				slopePositions.add(triggerPush.getEndSample());
				break;
			case DESCENDING:
				slopePositions.add(triggerPush.getEndSample());
				break;
			}
		}
		return slopePositions;
	}

}


