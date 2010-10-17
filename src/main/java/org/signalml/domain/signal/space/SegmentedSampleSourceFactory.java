/* SegmentedSampleSourceFactory.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;

/**
 * This class represents the factory (creator) of
 * {@link MultichannelSegmentedSampleSource segmented sources} of samples.
 * Allows to create continuous (not segmented) and segmented source based on
 * Only static shared instance is used.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SegmentedSampleSourceFactory {

	private static SegmentedSampleSourceFactory sharedInstance = null;

        /**
         * The default constructor.
         */
	protected SegmentedSampleSourceFactory() {

	}

        /**
         * Returns (and if necessary also creates) the shared factory
         * of segmented sources of samples
         * @return the shared factory of segmented sources of samples
         */
	public static SegmentedSampleSourceFactory getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new SegmentedSampleSourceFactory();
		}
		return sharedInstance;
	}

        /**
         * Tries to return the continuous (not segmented
         * {@link MultichannelSampleSource source} of samples. If it is not
         * possible returns the
         * {@link MultichannelSegmentedSampleSource segmented one}.
         * @param source the source of samples for the whole signal
         * @param signalSpace the {@link SignalSpace description} of the
         * parameters of the signal or the part of the signal in the
         * {@link MultichannelSampleSource source}
         * @param tagSet the set of tagged selections
         * @param pageSize the length of the page (in seconds)
         * @param blockSize the size of the page (in seconds)
         * @return the created source of samples
         */
	public MultichannelSampleSource getContinuousSampleSource(MultichannelSampleSource source, SignalSpace signalSpace, StyledTagSet tagSet, float pageSize, float blockSize) {

		if (signalSpace.getTimeSpaceType() == TimeSpaceType.WHOLE_SIGNAL && !signalSpace.isWholeSignalCompletePagesOnly()) {

			ChannelSpaceType channelSpaceType = signalSpace.getChannelSpaceType();
			return new ChannelSubsetSampleSource(source, channelSpaceType == ChannelSpaceType.WHOLE_SIGNAL ? null : signalSpace.getChannelSpace());

		}

		return getSegmentedSampleSource(source, signalSpace, tagSet, pageSize, blockSize);

	}

        /**
         * Creates the
         * {@link MultichannelSegmentedSampleSource segmented source} of
         * samples.
         * If time space is:
         * <ul>
         * <li>marker based - creates {@link MarkerSegmentedSampleSource}</li>
         * <li>selection based - creates {@link SelectionSegmentedSampleSource}</li>
         * <li>whole signal - creates {@link SelectionSegmentedSampleSource}, but
         * as a selection uses maximum possible page selection</li>
         * </ul>
         * @param source the source of samples for the whole signal
         * @param signalSpace the {@link SignalSpace description} of the
         * parameters of the signal or the part of the signal in the
         * {@link MultichannelSampleSource source}
         * @param tagSet the set of tagged selections
         * @param pageSize the length of the page (in seconds)
         * @param blockSize the size of the page (in seconds)
         * @return the created source of samples
         */
	public MultichannelSegmentedSampleSource getSegmentedSampleSource(MultichannelSampleSource source, SignalSpace signalSpace, StyledTagSet tagSet, float pageSize, float blockSize) {

		ChannelSpace channelSpace = null;
		if (signalSpace.getChannelSpaceType() == ChannelSpaceType.SELECTED) {
			channelSpace = signalSpace.getChannelSpace();
		}

		TimeSpaceType timeSpaceType = signalSpace.getTimeSpaceType();

		MultichannelSegmentedSampleSource sampleSource;
		SignalSelection selection;

		switch (timeSpaceType) {

		case MARKER_BASED :

			MarkerTimeSpace markerTimeSpace = signalSpace.getMarkerTimeSpace();
			sampleSource = new MarkerSegmentedSampleSource(source, tagSet, markerTimeSpace.getMarkerStyleName(), markerTimeSpace.getSecondsBefore(), markerTimeSpace.getSecondsAfter(), channelSpace);
			break;

		case SELECTION_BASED :

			selection = signalSpace.getSelectionTimeSpace();
			sampleSource = new SelectionSegmentedSampleSource(source, selection, channelSpace, pageSize, blockSize);
			break;

		case WHOLE_SIGNAL :

			int minSampleCount = SampleSourceUtils.getMinSampleCount(source);

			float time = ((float) minSampleCount) / source.getSamplingFrequency();

			selection = new SignalSelection(SignalSelectionType.PAGE, 0F, time);
			sampleSource = new SelectionSegmentedSampleSource(source, selection, channelSpace, pageSize, blockSize);

			break;

		default :
			throw new SanityCheckException("Unsupported type [" + timeSpaceType + "]");

		}

		return sampleSource;

	}

}
