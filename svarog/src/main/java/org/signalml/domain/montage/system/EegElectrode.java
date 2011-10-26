package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.math.geometry.Polar3dPoint;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegElectrode")
public class EegElectrode {

	private String label;
	private ChannelType channelType = ChannelType.PRIMARY;
	private Polar3dPoint polarPosition;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Polar3dPoint getPolarPosition() {
		return polarPosition;
	}

	public void setPolarPosition(Polar3dPoint polarPosition) {
		this.polarPosition = polarPosition;
	}

	@Override
	public String toString() {
		return label;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}

}
