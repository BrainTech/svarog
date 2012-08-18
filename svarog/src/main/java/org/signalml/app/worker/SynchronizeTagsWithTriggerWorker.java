package org.signalml.app.worker;

import javax.swing.SwingWorker;

import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.domain.signal.samplesource.OriginalMultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;

/**
 * A worker which synchronizes tags with trigger for a given document.
 *
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerWorker extends SwingWorker<Void, Void>{

	private static final int BUFFER_SIZE = 1024;

	private SignalDocument signalDocument;
	private double threshold;
	private int triggerChannel;

	private StyledTagSet tagSet;

	public SynchronizeTagsWithTriggerWorker(SynchronizeTagsWithTriggerParameters model) {
		super();
		this.signalDocument = model.getSignalDocument();
		this.threshold = model.getThresholdValue();
		this.triggerChannel = model.getTriggerChannel();

		this.tagSet = signalDocument.getActiveTag().getTagSet();
	}

	@Override
	protected Void doInBackground() throws Exception {
		OriginalMultichannelSampleSource sampleSource = signalDocument.getSampleSource();

		double[] samples = new double[BUFFER_SIZE];
		int sampleCount = sampleSource.getSampleCount(0);

		int currentSample = 0;
		for (Tag tag: tagSet.getTags()) {
			boolean tagRepositioned = false;

			while(!tagRepositioned && currentSample < sampleCount) {

				int bufferSize = BUFFER_SIZE;
				if (currentSample + bufferSize > sampleCount)
					bufferSize = sampleCount - currentSample;

				sampleSource.getSamples(triggerChannel, samples, currentSample, bufferSize, 0);

				int i = 1;
				for (; i < samples.length-1; i++) {
					if (samples[i+1] >= threshold && samples[i] < threshold) {
						double time = ((double)currentSample + i) / signalDocument.getSamplingFrequency();
						tag.setPosition(time);
						tagRepositioned = true;
						tagSet.editTag(tag);
						break;
					}
				}
				currentSample += i+1;

			}

		}

		return null;
	}

}
