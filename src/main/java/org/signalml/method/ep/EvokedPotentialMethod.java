/* EvokedPotentialMethod.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.exception.SignalMLException;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.util.ResolvableString;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EvokedPotentialMethod
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialMethod extends AbstractMethod implements TrackableMethod {

	protected static final Logger logger = Logger.getLogger(EvokedPotentialMethod.class);

	private static final String UID = "561691f8-bd14-486f-989b-a09c0bd57455";
	private static final String NAME = "evokedPotential";
	private static final int[] VERSION = new int[] {1,0};

	public EvokedPotentialMethod() throws SignalMLException {
		super();
	}

	@Override
	public Object doComputation(Object dataObj, final MethodExecutionTracker tracker) throws ComputationException {

		logger.debug("Beginning computation of EP");

		EvokedPotentialData data = (EvokedPotentialData) dataObj;

		tracker.setMessage(new ResolvableString("evokedPotentialMethod.message.preparing"));

		MultichannelSegmentedSampleSource sampleSource = data.getSampleSource();

		int sampleCount = sampleSource.getSegmentLength();
		int segmentCount = sampleSource.getSegmentCount();
		int channelCount = sampleSource.getChannelCount();
		float samplingFrequency = sampleSource.getSamplingFrequency();

		int i;
		int e;
		int j;

		double[] samples = new double[sampleCount];
		double[][] averageSamples = new double[channelCount][sampleCount];

		String[] labels = new String[channelCount];
		for (i=0; i<channelCount; i++) {
			labels[i] = sampleSource.getLabel(i);
		}

		EvokedPotentialResult result = new EvokedPotentialResult(data);

		SignalSpace signalSpace = data.getParameters().getSignalSpace();

		TimeSpaceType timeSpaceType = signalSpace.getTimeSpaceType();
		if (timeSpaceType == TimeSpaceType.MARKER_BASED) {

			MarkerTimeSpace markerTimeSpace = signalSpace.getMarkerTimeSpace();

			result.setSecondsBefore(markerTimeSpace.getSecondsBefore());
			result.setSecondsAfter(markerTimeSpace.getSecondsAfter());

		} else {

			// marker placed at the beginning of the segment
			result.setSecondsAfter(((double) sampleCount) / samplingFrequency);
			result.setSecondsBefore(0);

		}

		tracker.setMessage(new ResolvableString("evokedPotentialMethod.message.summing"));
		tracker.setTickerLimit(0, segmentCount);

		for (i=0; i<segmentCount; i++) {

			for (e=0; e<channelCount; e++) {

				if (tracker.isRequestingAbort()) {
					return null;
				}

				sampleSource.getSegmentSamples(e, samples, i);

				for (j=0; j<sampleCount; j++) {
					averageSamples[e][j] += samples[j];
				}

			}

			if (i % 10 == 0) {
				tracker.tick(0, 10);
			}

		}

		tracker.setMessage(new ResolvableString("evokedPotentialMethod.message.averaging"));
		tracker.setTicker(0, 0);
		tracker.setTickerLimit(0, channelCount);

		// markers have been summed, now divide to get the average

		for (e=0; e<channelCount; e++) {
			for (j=0; j<sampleCount; j++) {
				averageSamples[e][j] /= segmentCount;
			}
			tracker.tick(0);
		}


		result.setAverageSamples(averageSamples);
		result.setLabels(labels);
		result.setSampleCount(sampleCount);
		result.setChannelCount(channelCount);
		result.setSamplingFrequency(samplingFrequency);
		result.setAveragedCount(segmentCount);
		result.setSkippedCount(sampleSource.getUnusableSegmentCount());

		tracker.setMessage(new ResolvableString("evokedPotentialMethod.message.finished"));

		return result;

	}

	@Override
	public void validate(Object dataObj, Errors errors) {
		super.validate(dataObj, errors);
		if (!errors.hasErrors()) {
			EvokedPotentialData data = (EvokedPotentialData) dataObj;
			data.validate(errors);
		}
	}

	@Override
	public int getTickerCount() {
		return 1;
	}

	@Override
	public String getTickerLabel(MessageSourceAccessor messageSource, int ticker) {
		String code;
		switch (ticker) {

		case 0 :
			code = "evokedPotentialMethod.markerTicker";
			break;
		default :
			throw new IndexOutOfBoundsException();

		}
		return messageSource.getMessage(code);
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int[] getVersion() {
		return VERSION;
	}

	@Override
	public Object createData() {
		return new EvokedPotentialData();
	}

	@Override
	public Class<?> getResultClass() {
		return EvokedPotentialResult.class;
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return EvokedPotentialData.class.isAssignableFrom(clazz);
	}

}
