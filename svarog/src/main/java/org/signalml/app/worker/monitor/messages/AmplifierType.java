package org.signalml.app.worker.monitor.messages;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using=AmplifierTypeSerializer.class)
public enum AmplifierType {

	/*
	 * these should be ordered from the ones that
	 * load the fastest to the ones that are the slowest.
	 * It is because we want the {@link FindEEGExperimentsWorker}
	 * to send a request for the fastest ones first
	 * and when sending the requests, it iterates over these values.
	 */
	VIRTUAL("virtual"),
	USB("usb"),
	BLUETOOTH("bluetooth");

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
