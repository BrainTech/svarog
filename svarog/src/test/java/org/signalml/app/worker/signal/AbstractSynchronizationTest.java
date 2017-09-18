package org.signalml.app.worker.signal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.signalml.BaseTestCase;
import org.signalml.app.model.tag.SlopeType;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.domain.signal.samplesource.DoubleArraySampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.math.ArrayOperations;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public abstract class AbstractSynchronizationTest extends BaseTestCase {

	protected SynchronizeTagsWithTriggerParameters parameters;

	private final int sampleCount = 3000;

	protected List<TriggerPush> triggerPushes = new ArrayList<TriggerPush>();

	public AbstractSynchronizationTest() {
		triggerPushes.add(new TriggerPush(10, 15));
		triggerPushes.add(new TriggerPush(1024, 1400));
		triggerPushes.add(new TriggerPush(1500, 1600));
		triggerPushes.add(new TriggerPush(2523, 2524));
		triggerPushes.add(new TriggerPush(2550, 2551));
		triggerPushes.add(new TriggerPush(2599, 2700));
		triggerPushes.add(new TriggerPush(2800, 2900));
		triggerPushes.add(new TriggerPush(2901, 2950));

		parameters = new SynchronizeTagsWithTriggerParameters();

		parameters.setSampleSource(createSampleSource());
		parameters.setTagSet(createTagSet());
		parameters.setThresholdValue(1.0);
		parameters.setTriggerChannel(0);

	}

	protected MultichannelSampleSource createSampleSource() {

		double[][] samples = new double[2][sampleCount];

		for (int i = 0; i < triggerPushes.size(); i++) {
			TriggerPush triggerPush = triggerPushes.get(i);
			ArrayOperations.fillArrayWithValue(samples[0], 1.0, triggerPush.getStartSample(), triggerPush.getEndSample());
		}
		for (int i = 0; i < samples[1].length; i++) {
			samples[1][i] = Math.random();
		}

		DoubleArraySampleSource sampleSource = new DoubleArraySampleSource(samples);
		sampleSource.setSamplingFrequency(128.0F);

		return sampleSource;

	}

	protected StyledTagSet createTagSet() {

		StyledTagSet tagSet = new StyledTagSet();
		TagStyle tagStyle = new TagStyle(SignalSelectionType.CHANNEL, "style", "", Color.black, Color.blue, 1.0F);
		tagSet.addStyle(tagStyle);
		tagSet.addTag(new Tag(tagStyle, 1.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 2.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 4.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 5.0, 0.1));
		tagSet.addTag(new Tag(tagStyle, 6.0, 0.1));

		return tagSet;

	}

	public Integer[] getSlopePositions(SlopeType slopeType, int threshold) {
		List<Integer> slopes = new ArrayList<Integer>();
		for (TriggerPush triggerPush: triggerPushes) {
			if (triggerPush.getLength() < threshold)
				continue;

			switch (slopeType) {
			case ASCENDING:
				slopes.add(triggerPush.getStartSample());
				break;
			case BOTH:
				slopes.add(triggerPush.getStartSample());
				slopes.add(triggerPush.getEndSample());
				break;
			case DESCENDING:
				slopes.add(triggerPush.getEndSample());
				break;
			}
		}
		return slopes.toArray(new Integer[0]);
	}



}
