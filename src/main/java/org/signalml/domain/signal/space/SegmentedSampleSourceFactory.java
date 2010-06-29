/* SegmentedSampleSourceFactory.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SanityCheckException;

/** SegmentedSampleSourceFactory
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SegmentedSampleSourceFactory {

	private static SegmentedSampleSourceFactory sharedInstance = null;

	protected SegmentedSampleSourceFactory() {

	}

	public static SegmentedSampleSourceFactory getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new SegmentedSampleSourceFactory();
		}
		return sharedInstance;
	}

	public MultichannelSampleSource getContinuousSampleSource(MultichannelSampleSource source, SignalSpace signalSpace, StyledTagSet tagSet, float pageSize, float blockSize) {

		if (signalSpace.getTimeSpaceType() == TimeSpaceType.WHOLE_SIGNAL && !signalSpace.isWholeSignalCompletePagesOnly()) {

			ChannelSpaceType channelSpaceType = signalSpace.getChannelSpaceType();
			return new ChannelSubsetSampleSource(source, channelSpaceType == ChannelSpaceType.WHOLE_SIGNAL ? null : signalSpace.getChannelSpace());

		}

		return getSegmentedSampleSource(source, signalSpace, tagSet, pageSize, blockSize);

	}

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
