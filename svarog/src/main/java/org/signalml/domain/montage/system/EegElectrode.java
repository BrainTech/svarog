package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.math.geometry.Polar3dPoint;

/**
 * This class represents an EEG electrode that is a part of a specific
 * {@link EegSystem}.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegElectrode")
public class EegElectrode {

	/**
	 * The name of the electorode in the {@link EegSystem}.
	 */
	private String label;
	/**
	 * The type of signal the electrode is managing.
	 */
	private ChannelType channelType = ChannelType.PRIMARY;
	/**
	 * The position of the electrode on the head.
	 */
	private Polar3dPoint polarPosition;

	/**
	 * Returns the label of the electrode.
	 * @return the label of the electrode
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label for the electrode.
	 * @param label the label for the electrode
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the position of the electrode.
	 * @return the position of the electrode
	 */
	public Polar3dPoint getPolarPosition() {
		return polarPosition;
	}

	/**
	 * Sets the position of the electrode.
	 * @param polarPosition the position of the electrode
	 */
	public void setPolarPosition(Polar3dPoint polarPosition) {
		this.polarPosition = polarPosition;
	}

	/**
	 * Returns the {@link ChannelType} of the electrode.
	 * @return the {@link ChannelType} of the electrode
	 */
	public ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}

	@Override
	public String toString() {
		return label;
	}

}
