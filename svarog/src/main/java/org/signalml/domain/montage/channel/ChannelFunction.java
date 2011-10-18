package org.signalml.domain.montage.channel;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.config.preset.Preset;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("channelFunction")
public class ChannelFunction implements Preset {

	private String name;
	private String description;
	private Double maximumValue;
	private String unitOfMeasurement;

	public ChannelFunction() {
	}

	public ChannelFunction(String name, String description, double maximumValue, String unitOfMeasurement) {
		this.name = name;
		this.description = description;
		this.maximumValue = maximumValue;
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getMaximumValue() {
		return maximumValue;
	}

	public void setMaximumValue(Double maximumValue) {
		this.maximumValue = maximumValue;
	}

	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
