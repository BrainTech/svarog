/* TimeDomainSampleFilterEngineTest.java created 2010-08-28
 *
 */
package org.signalml.domain.signal;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.lang.Math.*;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;

/**
 * This class performs unit tests on the {@link MultichannelSampleFilter MultichannelSampleFilter} class.
 *
 * @author Piotr Szachewicz
 *
 */
public class MultichannelSampleFilterTest {

	/**
	 * the number of channels used for the tests
	 */
	public static final int TEST_CHANNEL_COUNT = 7;

	/**
	 * the number of samples used for the tests
	 */
	public static final int TEST_SAMPLE_COUNT = 10;

	/**
	 * the multichannel filter used for the tests
	 */
	private MultichannelSampleFilter mfilter;

	/**
	 * the multichannel source of samples which is fed into the MultichannelSampleFilter
	 */
	private RoundBufferMultichannelSampleSource source;

	/**
	 * Sets everything up for the tests.
	 */
	@Before
	public void setUp() {
		source = new RoundBufferMultichannelSampleSource(TEST_CHANNEL_COUNT, TEST_SAMPLE_COUNT);
		mfilter = new MultichannelSampleFilter(source, source);
	}

	/**
	 * Cleans up after the test.
	 */
	@After
	public void tearDown() {
		source = null;
		mfilter = null;
	}

	/**
	 * Test method for {@link org.signalml.domain.signal.MultichannelSampleFilter#addFilter(SampleFilterEngine)}.
	 */
	@Test
	public void testAddFilterAllChannels() {
		TimeDomainSampleFilter definition = new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
		                new double[] {20, 0}, new double[] {30, 8}, 5.0, 20.0);
		definition.setSamplingFrequency(128.0);
		TimeDomainSampleFilterEngine filterEngine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0), definition);

		int i, j;

		//adds one filter on each step and checks if the filter chains' sizes are correct
		for (i = 0; i < 4; i++) {
			for (j = 0; j < TEST_CHANNEL_COUNT; j++)
				assertEquals(mfilter.chains.elementAt(j).size(), i);
			mfilter.addFilter(filterEngine);
		}

	}

	/**
	 * Test method for {@link org.signalml.domain.signal.MultichannelSampleFilter#addFilter(SampleFilterEngine, int[])}.
	 */
	@Test
	public void testAddFilterForSpecifiedChannels() {
		TimeDomainSampleFilter definition = new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
		                new double[] {20, 0}, new double[] {30, 8}, 5.0, 20.0);
		definition.setSamplingFrequency(128.0);
		TimeDomainSampleFilterEngine filterEngine = new TimeDomainSampleFilterEngine(new ChannelSelectorSampleSource(source,0), definition);

		int i, j;

		//checking if the filter chains are empty
		for (j = 0; j < TEST_CHANNEL_COUNT; j++)
			assertEquals(mfilter.chains.elementAt(j).size(), 0);

		//adding  one filter to channels 2,3  & validating chains sizes
		mfilter.addFilter(filterEngine, new int[] {2, 3});
		for (j = 0; j < TEST_CHANNEL_COUNT; j++) {
			if (j == 2 || j == 3)
				assertEquals(mfilter.chains.elementAt(j).size(), 1);
			else
				assertEquals(mfilter.chains.elementAt(j).size(), 0);
		}

		//adding one filter to channels 3,4,5 & validating chains sizes
		mfilter.addFilter(filterEngine, new int[] {3, 4, 5});
		for (j = 0; j < TEST_CHANNEL_COUNT; j++) {

			if (j == 2 || j == 4 || j == 5)
				assertEquals(mfilter.chains.elementAt(j).size(), 1);
			else if (j == 3)
				assertEquals(mfilter.chains.elementAt(j).size(), 2);
			else
				assertEquals(mfilter.chains.elementAt(j).size(), 0);

		}

	}

	/**
	 * Test method for {@link org.signalml.domain.signal.MultichannelSampleFilter#applyMontage(Montage)}.
	 */
	@Test
	public void testApplyMontage() throws MontageMismatchException {
		int i;

		SourceMontage sMontage = new SourceMontage(SignalType.EEG_10_20, TEST_CHANNEL_COUNT);
		Montage montage = new Montage(sMontage);
		for (i = 0; i < TEST_CHANNEL_COUNT; i++)
			montage.addMontageChannel(i, i);

		//no filters
		mfilter.applyMontage(montage);

		for (i = 0; i < TEST_CHANNEL_COUNT; i++)
			assertEquals(mfilter.chains.elementAt(i).size(), 0);

		//two filters in a montage
		SampleFilterDefinition definition[] = {new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
		                                       new double[] {20, 0}, new double[] {30, 8}, 5.0, 20.0),
		                                       new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
		                                                                  new double[] {20, 0}, new double[] {30, 8}, 5.0, 10.0),
		                                       new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
		                                                                  new double[] {20, 0}, new double[] {30, 8}, 5.0, 15.0),
		                                       new FFTSampleFilter(true),
		                                       new FFTSampleFilter(true)
		                                      };

		/*MultichannelSampleFilter sums all FFT filters ranges into one FFT filter
		 *summaryFFT filter is used to check if that functionality works
		 */
		FFTSampleFilter summaryFFT = new FFTSampleFilter(true);
		Range range = ((FFTSampleFilter)definition[3]).new Range(10, 20, 0.5);
		((FFTSampleFilter)definition[3]).setRange(range);
		summaryFFT.setRange(range);
		range = ((FFTSampleFilter)definition[3]).new Range(50, 60, 0);
		((FFTSampleFilter)definition[4]).setRange(range);
		summaryFFT.setRange(range);

		montage.addSampleFilter(definition[0]);
		montage.addSampleFilter(definition[1]);
		mfilter.applyMontage(montage);

		for (i = 0; i < TEST_CHANNEL_COUNT; i++)
			assertEquals(mfilter.chains.elementAt(i).size(), 2);

		//third filter but excluded on one channel
		montage.addSampleFilter(definition[2]);
		montage.setFilterChannelExcluded(2, 2, true);
		mfilter.applyMontage(montage);

		for (i = 0; i < TEST_CHANNEL_COUNT; i++) {

			if (i == 2)
				assertEquals(mfilter.chains.elementAt(i).size(), 2);
			else
				assertEquals(mfilter.chains.elementAt(i).size(), 3);

		}

		//adding FFT Filters
		montage.addSampleFilter(definition[3]);
		montage.addSampleFilter(definition[4]);
		mfilter.applyMontage(montage);

		for (i = 0; i < 1; i++) {
			assertTrue(definition[0].equals(mfilter.chains.get(i).get(0).getFilterDefinition()));
			assertTrue(definition[1].equals(mfilter.chains.get(i).get(1).getFilterDefinition()));

			if (i==2) //second filter is excluded on the second channel
				assertTrue(summaryFFT.equals(mfilter.chains.get(i).get(2).getFilterDefinition()));
			else {
				assertTrue(definition[2].equals(mfilter.chains.get(i).get(2).getFilterDefinition()));
				assertTrue(summaryFFT.equals(mfilter.chains.get(i).get(3).getFilterDefinition()));
			}

		}

	}


}
