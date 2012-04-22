package org.signalml.app.worker.monitor.messages;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using=AmplifierTypeSerializer.class)
public enum AmplifierType {

	BLUETOOTH("bt"), //TODO -- poprosić Asię, żeby zmieniła to na BLUETOOTH
	USB("usb"),
	VIRTUAL("virtual");

	private String code;

	private AmplifierType(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}

}

class AmplifierTypeSerializer extends JsonSerializer<AmplifierType> {

	@Override
	public void serialize(AmplifierType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(value.toString());
	}

}
