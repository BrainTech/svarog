/* SignalSpace.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.space;

import java.io.Serializable;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.Tag;

/**
 * This class describes the parameters of the signal or the part of the signal
 * in the {@link MultichannelSampleSource source}.
 * For these parameters see "See also".
 *
 * @see ChannelSpace
 * @see ChannelSpaceType
 * @see MarkerTimeSpace
 * @see SignalSelection
 * @see SignalSourceLevel
 * @see TimeSpaceType
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSpace implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalSourceLevel level} describing how the signal
	 * was processed
	 */
	private SignalSourceLevel signalSourceLevel;

	/**
	 * describes the {@link TimeSpaceType time} selection used
	 */
	private TimeSpaceType timeSpaceType;
	/**
	 * if the whole signal is used or only selected channels
	 */
	private ChannelSpaceType channelSpaceType;

	/**
	 * true if the whole signal has only complete pages, false otherwise
	 */
	private boolean wholeSignalCompletePagesOnly;
	/**
	 * describes the selected part of the signal if the time is based
	 * on the signal selection
	 */
	private SignalSelection selectionTimeSpace;
	/**
	 * describes the selected part of the signal if the time is based
	 * on the neighbourhood of the marker
	 */
	private MarkerTimeSpace markerTimeSpace;

	/**
	 * the {@link ChannelSpace subset} of channels that is used
	 */
	private ChannelSpace channelSpace;

	/**
	 * Constructor. Creates the default description of parameters of the
	 * source of samples.
	 */
	public SignalSpace() {
		signalSourceLevel = SignalSourceLevel.FILTERED;
		timeSpaceType = TimeSpaceType.WHOLE_SIGNAL;
		channelSpaceType = ChannelSpaceType.WHOLE_SIGNAL;
		wholeSignalCompletePagesOnly = false;
	}

	/**
	 * Returns the {@link SignalSourceLevel level} describing how the signal
	 * was processed.
	 * @return the level describing how the signal was processed
	 */
	public SignalSourceLevel getSignalSourceLevel() {
		return signalSourceLevel;
	}

	/**
	 * Sets the {@link SignalSourceLevel level} describing how the signal
	 * was processed.
	 * @param signalSourceLevel the level describing how the signal
	 * was processed
	 */
	public void setSignalSourceLevel(SignalSourceLevel signalSourceLevel) {
		this.signalSourceLevel = signalSourceLevel;
	}

	/**
	 * Returns the description of the {@link TimeSpaceType time}
	 * selection used.
	 * @return the description of the time selection used
	 */
	public TimeSpaceType getTimeSpaceType() {
		return timeSpaceType;
	}

	/**
	 * Sets the description of the {@link TimeSpaceType time}
	 * selection used.
	 * @param timeSpaceType the description of the time selection used
	 */
	public void setTimeSpaceType(TimeSpaceType timeSpaceType) {
		this.timeSpaceType = timeSpaceType;
	}

	/**
	 * Returns if the whole signal is used or only selected channels
	 * @return if the whole signal is used or only selected channels
	 * @see ChannelSpaceType
	 */
	public ChannelSpaceType getChannelSpaceType() {
		return channelSpaceType;
	}

	/**
	 * Sets if the whole signal is used or only selected channels.
	 * @param channelSpaceType if the whole signal is used or only selected
	 * channels
	 */
	public void setChannelSpaceType(ChannelSpaceType channelSpaceType) {
		this.channelSpaceType = channelSpaceType;
	}

	/**
	 * Returns the description of the selected part of the signal if the
	 * time is based on the signal selection.
	 * @return the description of the selected part of the signal if the
	 * time is based on the signal selection
	 */
	public SignalSelection getSelectionTimeSpace() {
		return selectionTimeSpace;
	}

	/**
	 * Sets the description of the selected part of the signal if the
	 * time is based on the signal selection.
	 * @param selectionTimeSpace the description of the selected part of
	 * the signal if the time is based on the signal selection
	 */
	public void setSelectionTimeSpace(SignalSelection selectionTimeSpace) {
		this.selectionTimeSpace = selectionTimeSpace;
	}

	/**
	 * Returns the description of the selected part of the signal if the
	 * time is based on the neighbourhood of the marker.
	 * @return the description of the selected part of the signal if
	 * the time is based on the neighbourhood of the marker
	 */
	public MarkerTimeSpace getMarkerTimeSpace() {
		return markerTimeSpace;
	}

	/**
	 * Sets the description of the selected part of the signal if the time
	 * is based on the neighbourhood of the marker.
	 * @param markerTimeSpace the description of the selected part of
	 * the signal if the time is based on the neighbourhood of the marker
	 */
	public void setMarkerTimeSpace(MarkerTimeSpace markerTimeSpace) {
		this.markerTimeSpace = markerTimeSpace;
	}

	/**
	 * Returns the {@link ChannelSpace subset} of channels that is used.
	 * @return the subset of channels that is used
	 */
	public ChannelSpace getChannelSpace() {
		return channelSpace;
	}

	/**
	 * Sets the {@link ChannelSpace subset} of channels that is used.
	 * @param channelSpace the subset of channels that is used
	 */
	public void setChannelSpace(ChannelSpace channelSpace) {
		this.channelSpace = channelSpace;
	}

	/**
	 * Returns if the whole signal has only complete pages
	 * @return true if the whole signal has only complete pages,
	 * false otherwise
	 */
	public boolean isWholeSignalCompletePagesOnly() {
		return wholeSignalCompletePagesOnly;
	}

	/**
	 * Sets if the whole signal has only complete pages
	 * @param wholeSignalCompletePagesOnly true if the whole signal has only
	 * complete pages, false otherwise
	 */
	public void setWholeSignalCompletePagesOnly(boolean wholeSignalCompletePagesOnly) {
		this.wholeSignalCompletePagesOnly = wholeSignalCompletePagesOnly;
	}

	/**
	 * Configures the parameters of this description based on a given
	 * {@link SignalSelection selection} or a given {@link Tag tag}.
	 * @param signalSelection the selection of the part of the signal
	 * @param tagSelection the tagged selection of the part of the signal
	 */
	public void configureFromSelections(SignalSelection signalSelection, Tag tagSelection) {

		if (signalSelection != null) {

			setTimeSpaceType(TimeSpaceType.SELECTION_BASED);
			setSelectionTimeSpace(signalSelection);

			if (signalSelection.getType().isChannel()) {

				setChannelSpaceType(ChannelSpaceType.SELECTED);

				ChannelSpace channelSpace = new ChannelSpace();
				channelSpace.addChannel(signalSelection.getChannel());

				setChannelSpace(channelSpace);

			} else {

				setChannelSpaceType(ChannelSpaceType.WHOLE_SIGNAL);
				setChannelSpace(null);
			}

		} else {

			if (tagSelection != null) {
				if (tagSelection.isMarker() && tagSelection.getType().isChannel()) {

					setTimeSpaceType(TimeSpaceType.MARKER_BASED);

					MarkerTimeSpace markerTimeSpace = new MarkerTimeSpace();
					markerTimeSpace.setMarkerChannel(tagSelection.getChannel());
					markerTimeSpace.setMarkerStyleName(tagSelection.getStyle().getName());

					markerTimeSpace.setSecondsAfter(1.0);
					markerTimeSpace.setSecondsBefore(0.0);

					setMarkerTimeSpace(markerTimeSpace);

					setChannelSpaceType(ChannelSpaceType.WHOLE_SIGNAL);
					setChannelSpace(null);

				}

			}

		}


	}

}
