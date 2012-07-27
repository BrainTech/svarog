/* EvokedPotentialMethod.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.iir.IIRFilterEngine;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.math.iirdesigner.IIRDesigner;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.plugin.export.SignalMLException;
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

		tracker.setMessage(_("Preparing"));

		MultichannelSegmentedSampleSource sampleSource = data.getSampleSources().get(0);

		int sampleCount = sampleSource.getSegmentLength();
		int segmentCount = sampleSource.getSegmentCount();
		int channelCount = sampleSource.getChannelCount();
		float samplingFrequency = sampleSource.getSamplingFrequency();

		String[] labels = new String[channelCount];
		for (int segment=0; segment<channelCount; segment++) {
			labels[segment] = sampleSource.getLabel(segment);
		}

		EvokedPotentialResult result = new EvokedPotentialResult(data);

		EvokedPotentialParameters parameters = data.getParameters();
		result.setSecondsBefore(parameters.getAveragingTimeBefore());
		result.setSecondsAfter(parameters.getAveragingTimeAfter());

		tracker.setMessage(_("Summing"));
		tracker.setTickerLimit(0, segmentCount);

		for (MultichannelSegmentedSampleSource segmentedSampleSource: data.getSampleSources()) {
			double[][] averageSamples = average(segmentedSampleSource, tracker);
			if (averageSamples == null)
				return null;
			result.addAverageSamples(averageSamples);
		}

		if (data.getParameters().isBaselineCorrectionEnabled())
			performBaselineCorrection(result, data);

		if (data.getParameters().isFilteringEnabled())
			try {
				performLowPassFiltering(result, data);
			} catch (BadFilterParametersException exception) {
				exception.printStackTrace();
				throw new ComputationException(_("An error occured while designing the low pass filter."));
			}

		result.setLabels(labels);
		result.setSampleCount(sampleCount);
		result.setChannelCount(channelCount);
		result.setSamplingFrequency(samplingFrequency);
		result.setAveragedCount(segmentCount);
		result.setSkippedCount(sampleSource.getUnusableSegmentCount());

		tracker.setMessage(_("Finished"));

		return result;

	}

	protected void performBaselineCorrection(EvokedPotentialResult result, EvokedPotentialData data) {
		int sampleSourceNumber = 0;
		for (MultichannelSegmentedSampleSource segmentedSampleSource: data.getBaselineSampleSources()) {
			double[] baselineSamples = new double[segmentedSampleSource.getSegmentLength()];
			for (int channel = 0; channel < segmentedSampleSource.getChannelCount(); channel++) {
				double sum = 0.0;
				for (int segment = 0; segment < segmentedSampleSource.getSegmentCount(); segment++) {
					segmentedSampleSource.getSegmentSamples(channel, baselineSamples, segment);

					for (double sample: baselineSamples) {
						sum += sample;
					}
				}
				double baseline = sum / (segmentedSampleSource.getSegmentCount() * segmentedSampleSource.getSegmentLength());

				if (result.getAverageSamples().size() < sampleSourceNumber) {
					double[] samples = result.getAverageSamples().get(sampleSourceNumber)[channel];
					for (int i = 0; i < samples.length; i++)
						samples[i] = samples[i] - baseline;
				}
			}
			sampleSourceNumber++;
		}
	}

	protected void performLowPassFiltering(EvokedPotentialResult result, EvokedPotentialData data) throws BadFilterParametersException {

		double cutoffFrequency = data.getParameters().getFilterCutOffFrequency();
		double stopbandFrequency = cutoffFrequency * 2;

		TimeDomainSampleFilter lowpassFilter = new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
				new double[] {cutoffFrequency, 0.0}, new double[] {stopbandFrequency, 0.0}, 3.0, 30);
		lowpassFilter.setSamplingFrequency(data.getSampleSources().get(0).getSamplingFrequency());

		FilterCoefficients filterCoefficients = IIRDesigner.designDigitalFilter(lowpassFilter);

		for (double[][] samples: result.getAverageSamples()) {
			for (int channel = 0; channel < samples.length; channel++) {
				IIRFilterEngine engine = new IIRFilterEngine(filterCoefficients.getBCoefficients(), filterCoefficients.getACoefficients());
				samples[channel] = engine.filter(samples[channel]);
			}
		}
	}

	protected double[][] average(MultichannelSegmentedSampleSource sampleSource, MethodExecutionTracker tracker) {
		int sampleCount = sampleSource.getSegmentLength();
		int segmentCount = sampleSource.getSegmentCount();
		int channelCount = sampleSource.getChannelCount();

		double[] samples = new double[sampleCount];
		double[][] averageSamples = new double[channelCount][sampleCount];

		for (int segment=0; segment<segmentCount; segment++) {

			for (int channel=0; channel<channelCount; channel++) {

				if (tracker.isRequestingAbort()) {
					return null;
				}

				sampleSource.getSegmentSamples(channel, samples, segment);

				for (int j=0; j<sampleCount; j++) {
					averageSamples[channel][j] += samples[j];
				}

			}

			if (segment % 10 == 0) {
				tracker.tick(0, 10);
			}

		}

		tracker.setMessage(_("Averaging"));
		tracker.setTicker(0, 0);
		tracker.setTickerLimit(0, channelCount);

		// markers have been summed, now divide to get the average
		if (segmentCount >0 ) {
			for (int channel=0; channel<channelCount; channel++) {
				for (int j=0; j<sampleCount; j++) {
					averageSamples[channel][j] /= segmentCount;
				}
				tracker.tick(0);
			}
		}

		return averageSamples;
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
	public String getTickerLabel(int ticker) {
		if (0 == ticker)
			return _("Processing markers");
		else
			throw new IndexOutOfBoundsException();
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
